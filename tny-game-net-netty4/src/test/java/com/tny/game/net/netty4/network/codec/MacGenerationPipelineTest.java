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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * add-mac-generation-siphash 3.1：认证代次（SipHash24CodecVerifier + XorTileCodecCrypto）
 * 经生产 NetPacketV1 编解码管线的全场景认领（specs 六 Scenario）。
 */
class MacGenerationPipelineTest {

    private static final String BODY = "mac-generation-payload-0123456789-abcdefghij";

    /** (a) 认证代次报文正常收发，校验码 8B、布局不变 */
    @Test
    void macGenerationRoundTrips() throws Exception {
        Fixture f = new Fixture(Generation.MAC);
        ByteBuf out = Unpooled.buffer(8192);
        f.encoder.encodeObject(f.ctx, f.request(BODY), out);

        byte option = out.getByte(out.readerIndex() + 4);
        assertTrue(CodecConstants.isOption(option, CodecConstants.DATA_PACK_OPTION_VERIFY), "校验位应声明");
        assertTrue(CodecConstants.isOption(option, CodecConstants.DATA_PACK_OPTION_ENCRYPT), "混淆位应声明");
        assertEquals(8, f.decoderConfigVerifyCodeLength(), "认证代次校验码 8B——与现 CRC64 等长");

        Message decoded = f.decoder.decodeObject(f.ctx, out, new NetPacketDecodeMarker());
        assertEquals(BODY, decoded.getBody(), "全管线往返应还原");
    }

    /** (b) 篡改载荷任意字节 → causeVerify */
    @Test
    void tamperedPayloadRejected() throws Exception {
        Fixture f = new Fixture(Generation.MAC);
        ByteBuf out = Unpooled.buffer(8192);
        f.encoder.encodeObject(f.ctx, f.request(BODY), out);
        out.setByte(out.readerIndex() + 20, out.getByte(out.readerIndex() + 20) ^ 0x5A);

        NetCodecException thrown = assertThrows(NetCodecException.class,
                () -> f.decoder.decodeObject(f.ctx, out, new NetPacketDecodeMarker()));
        assertTrue(thrown.getMessage().toLowerCase().contains("verify"), "必须因键控校验失败被拒");
    }

    /** (c) 换皮攻击：两帧载荷等长时互换帧尾校验码 → 双杀（MAC 绑定 number 与载荷） */
    @Test
    void swappedTrailerRejected() throws Exception {
        Fixture f = new Fixture(Generation.MAC);
        ByteBuf a = Unpooled.buffer(8192);
        f.encoder.encodeObject(f.ctx, f.request(BODY), a);
        ByteBuf b = Unpooled.buffer(8192);
        f.encoder.encodeObject(f.ctx, f.request(BODY), b);
        assertEquals(a.readableBytes(), b.readableBytes(), "同报文两帧等长方可互换");
        int len = a.readableBytes();
        byte[] trailerB = new byte[8];
        b.getBytes(b.readerIndex() + len - 8, trailerB);
        a.setBytes(a.readerIndex() + len - 8, trailerB); // a 的载荷+其 number 配 b 的 MAC（不同 number）

        NetCodecException thrown = assertThrows(NetCodecException.class,
                () -> f.decoder.decodeObject(f.ctx, a, new NetPacketDecodeMarker()),
                "校验码与 number 绑定：换皮/拼帧必须被拒");
        assertTrue(thrown.getMessage().toLowerCase().contains("verify"));
    }

    /** (d) 键流等价：同配置下 MAC 代次的混淆输出与 legacy 代次逐字节一致，仅校验码 8B 不同 */
    @Test
    void keystreamIdenticalToLegacyGeneration() throws Exception {
        Fixture legacy = new Fixture(Generation.LEGACY);
        Fixture mac = new Fixture(Generation.MAC);
        // 共享同一 Message（time 字段固定），两代次仅校验码算法不同——否则 time 毫秒差污染等价断言
        Message shared = mac.request(BODY);
        ByteBuf a = Unpooled.buffer(8192);
        legacy.encoder.encodeObject(legacy.ctx, shared, a);
        ByteBuf b = Unpooled.buffer(8192);
        mac.encoder.encodeObject(mac.ctx, shared, b);

        int len = a.readableBytes();
        assertEquals(len, b.readableBytes(), "帧长必须一致（校验码等长）");
        byte[] ba = new byte[len - 8];
        byte[] bb = new byte[len - 8];
        a.getBytes(a.readerIndex(), ba);
        b.getBytes(b.readerIndex(), bb);
        assertArrayEquals(ba, bb, "除校验码外所有帧字节必须逐字节一致（键流等价 → 零 wire 语义影响）");
        assertFalse(Arrays.equals(last8(a), last8(b)), "校验码本体必须不同（CRC64 vs 键控 MAC）");
    }

    /** (f) 混布代次错配：任一侧单独切换 → 可观测校验拒绝，不静默误解密 */
    @Test
    void mixedGenerationRejectedBothDirections() throws Exception {
        Fixture macSide = new Fixture(Generation.MAC);
        Fixture legacySide = new Fixture(Generation.LEGACY);
        ByteBuf out = Unpooled.buffer(8192);
        macSide.encoder.encodeObject(macSide.ctx, macSide.request(BODY), out);
        NetCodecException thrown = assertThrows(NetCodecException.class,
                () -> legacySide.decoder.decodeObject(legacySide.ctx, out, new NetPacketDecodeMarker()),
                "legacy 解码端必须拒绝认证代次帧");
        assertTrue(thrown.getMessage().toLowerCase().contains("verify"));

        ByteBuf out2 = Unpooled.buffer(8192);
        legacySide.encoder.encodeObject(legacySide.ctx, legacySide.request(BODY), out2);
        assertThrows(NetCodecException.class,
                () -> macSide.decoder.decodeObject(macSide.ctx, out2, new NetPacketDecodeMarker()),
                "认证代次解码端必须拒绝 legacy 帧");
    }

    private static byte[] last8(ByteBuf buf) {
        byte[] tail = new byte[8];
        buf.getBytes(buf.readerIndex() + buf.readableBytes() - 8, tail);
        return tail;
    }

    private enum Generation {LEGACY, MAC}

    /** verify+encrypt 双开、waste 关（legacy 口径）；两代次仅 verifier/crypto 换件 */
    private static final class Fixture {
        final EmbeddedChannel channel = new EmbeddedChannel();
        final NetTunnel tunnel = mock(NetTunnel.class);
        final ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        final NetPacketV1Encoder encoder;
        final NetPacketV1Decoder decoder;
        final NetPacketCodecSetting decoderConfig;
        final CommonMessageFactory messageFactory = new CommonMessageFactory();

        Fixture(Generation generation) {
            NetPacketCodecSetting config = new NetPacketCodecSetting();
            config.setVerifyEnable(true);
            config.setEncryptEnable(true);
            config.setWasteBytesEnable(false);
            config.setSecurityKeys(new String[]{"bench-mac-gen-key"});

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

            CodecVerifier verifier = generation == Generation.MAC ? new SipHash24CodecVerifier() : new CRC64CodecVerifier();
            CodecCrypto crypto = generation == Generation.MAC ? new XorTileCodecCrypto() : new XOrCodecCrypto();

            encoder = new NetPacketV1Encoder(config);
            encoder.setMessageCodec(messageCodec);
            encoder.verifier = verifier;
            encoder.crypto = crypto;

            decoderConfig = config;
            decoder = new NetPacketV1Decoder(config);
            decoder.setMessageCodec(messageCodec);
            decoder.verifier = verifier;
            decoder.crypto = crypto;
        }

        int decoderConfigVerifyCodeLength() {
            return decoder.verifier.getCodeLength();
        }

        Message request(String body) {
            return messageFactory.create(1L, MessageContents.push(Protocols.protocol(1000), body));
        }
    }
}
