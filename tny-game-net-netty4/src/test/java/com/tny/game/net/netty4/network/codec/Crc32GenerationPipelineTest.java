/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * add-crc32-verify-tier 3.1：快筛代次经生产管线的全场景认领（specs 五 Scenario）。
 * 混淆件两侧统一用 xorTile（与 XOr 逐字节等价，测试互证）以隔离变量——只观察校验码代次差异（帧长 -4B 与拒绝行为）。
 */
class Crc32GenerationPipelineTest {

    private static final String BODY = "crc32-tier-pipeline-payload-0123456789-abcdefgh";

    /** (a)(b) 同批代次往返 + 帧长恰短 4B + 前段字节一致 + 校验码区不同 */
    @Test
    void fourByteTrailerAndFrameDelta() throws Exception {
        Fixture legacy = new Fixture(Generation.LEGACY);
        Fixture fast = new Fixture(Generation.CRC32);
        Message shared = fast.request(BODY);

        ByteBuf a = Unpooled.buffer(8192);
        legacy.encoder.encodeObject(legacy.ctx, shared, a);
        ByteBuf b = Unpooled.buffer(8192);
        fast.encoder.encodeObject(fast.ctx, shared, b);

        int la = a.readableBytes();
        int lb = b.readableBytes();
        assertEquals(la - 4, lb, "快筛代次帧尾 4B，总长应恰短 4 字节");

        // 帧 = [magic4‖option1][payloadLength fixed32][accessId+number+载荷体][校验码尾]
        // 两代次仅 payloadLength 值与尾长不同：区段一（前 5B）与区段二（长度字段后至尾前）必须一致
        byte[] preA = new byte[5];
        byte[] preB = new byte[5];
        a.getBytes(a.readerIndex(), preA);
        b.getBytes(b.readerIndex(), preB);
        assertArrayEquals(preA, preB, "magic+option 前缀必须一致（布局不变）");
        int bodySpan = lb - 4 - 9; // b 的 body 区 [9, lb-4)
        byte[] midA = new byte[bodySpan];
        byte[] midB = new byte[bodySpan];
        a.getBytes(a.readerIndex() + 9, midA);
        b.getBytes(b.readerIndex() + 9, midB);
        assertArrayEquals(midA, midB, "长度字段与校验码之间所有帧字节必须一致（同键流同混入）");
        assertNotEquals(a.getInt(a.readerIndex() + 5), b.getInt(b.readerIndex() + 5), "payloadLength 应恰差 4");

        Message decoded = fast.decoder.decodeObject(fast.ctx, b, new NetPacketDecodeMarker());
        assertEquals(BODY, decoded.getBody(), "同批代次全管线往返必须还原");
    }

    /** (c) 篡改载荷 → 可观察拒绝 */
    @Test
    void tamperedPayloadRejected() throws Exception {
        Fixture f = new Fixture(Generation.CRC32);
        ByteBuf out = Unpooled.buffer(8192);
        f.encoder.encodeObject(f.ctx, f.request(BODY), out);
        out.setByte(out.readerIndex() + 18, out.getByte(out.readerIndex() + 18) ^ 0x5A);
        assertThrows(NetCodecException.class,
                () -> f.decoder.decodeObject(f.ctx, out, new NetPacketDecodeMarker()));
    }

    /** (d) 代次错配双向：4B 帧→8B 端 与 8B 帧→4B 端，均 MUST 可观察拒绝、不静默误解密 */
    @Test
    void mixedGenerationRejectedBothDirections() throws Exception {
        Fixture legacy = new Fixture(Generation.LEGACY);
        Fixture fast = new Fixture(Generation.CRC32);

        ByteBuf fastFrame = Unpooled.buffer(8192);
        fast.encoder.encodeObject(fast.ctx, fast.request(BODY), fastFrame);
        assertThrows(NetCodecException.class,
                () -> legacy.decoder.decodeObject(legacy.ctx, fastFrame, new NetPacketDecodeMarker()),
                "8B 解码端遇 4B 帧必须可观察拒绝");

        ByteBuf legacyFrame = Unpooled.buffer(8192);
        legacy.encoder.encodeObject(legacy.ctx, legacy.request(BODY), legacyFrame);
        assertThrows(NetCodecException.class,
                () -> fast.decoder.decodeObject(fast.ctx, legacyFrame, new NetPacketDecodeMarker()),
                "4B 解码端遇 8B 帧必须可观察拒绝");
    }

    /** (e) 默认装配零影响冒烟：legacy 代次往返与校验码 8B 保持 */
    @Test
    void legacyDefaultUnaffectedSmoke() throws Exception {
        Fixture legacy = new Fixture(Generation.LEGACY);
        assertEquals(8, legacy.decoder.verifier.getCodeLength(), "默认代次校验码长度不变");
        ByteBuf out = Unpooled.buffer(8192);
        legacy.encoder.encodeObject(legacy.ctx, legacy.request(BODY), out);
        Message decoded = legacy.decoder.decodeObject(legacy.ctx, out, new NetPacketDecodeMarker());
        assertEquals(BODY, decoded.getBody());
    }

    private enum Generation {LEGACY, CRC32}

    private static final class Fixture {
        final EmbeddedChannel channel = new EmbeddedChannel();
        final NetTunnel tunnel = mock(NetTunnel.class);
        final ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        final NetPacketV1Encoder encoder;
        final NetPacketV1Decoder decoder;
        final CommonMessageFactory messageFactory = new CommonMessageFactory();

        Fixture(Generation generation) {
            NetPacketCodecSetting config = new NetPacketCodecSetting();
            config.setVerifyEnable(true);
            config.setEncryptEnable(true);
            config.setWasteBytesEnable(false);
            config.setSecurityKeys(new String[]{"bench-crc32-tier-key"});

            NettyMessageCodec messageCodec = new DefaultNettyMessageCodec(new MessageBodyCodec<String>() {
                @Override
                public String decode(ByteBuf buffer) {
                    byte[] bytes = new byte[buffer.readableBytes()];
                    buffer.getBytes(buffer.readerIndex(), bytes);
                    return new String(bytes, StandardCharsets.UTF_8);
                }

                @Override
                public void encode(String object, ByteBuf code) {
                    code.writeBytes(object.getBytes(StandardCharsets.UTF_8));
                }
            }, new DefaultMessageHeaderCodec());

            when(tunnel.getAccessId()).thenReturn(999999L);
            when(tunnel.getMessageFactory()).thenReturn(messageFactory);
            channel.attr(NettyNetAttrKeys.TUNNEL).set(tunnel);
            when(ctx.channel()).thenReturn(channel);

            CodecVerifier verifier = generation == Generation.CRC32 ? new Crc32CodecVerifier() : new CRC64CodecVerifier();
            CodecCrypto crypto = new XorTileCodecCrypto();

            encoder = new NetPacketV1Encoder(config);
            encoder.setMessageCodec(messageCodec);
            encoder.verifier = verifier;
            encoder.crypto = crypto;

            decoder = new NetPacketV1Decoder(config);
            decoder.setMessageCodec(messageCodec);
            decoder.verifier = verifier;
            decoder.crypto = crypto;
        }

        Message request(String body) {
            return messageFactory.create(1L, MessageContents.push(Protocols.protocol(1000), body));
        }
    }
}
