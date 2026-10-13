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
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * fix-xor-crypto-scope 1.2：ENCRYPT 声明与线上字节一致性（net-protocol 新增需求集成面）。
 * 使用专用 PooledByteBufAllocator 并以占位分配迫使载荷缓冲窗口起点非零——
 * 修复前该形态下"加密"整段跳过（明文上线），全窗用例应红；修复后全窗密文+往返+篡改拒绝应绿。
 * 装配沿用 PacketGateTest 夹具（同包 protected 字段直挂 verifier/crypto）。
 */
class NetPacketEncryptScopeTest {

    private static final String BODY = "hello-encrypt-scope-payload-0123456789";

    /**
     * 生产调用形态对拍（确定性红基线）：加密侧窗口起点 13（池化常态）、解密侧窗口起点 0
     * （另一端的池状态独立）——修复前加密整段跳过/相位错位，此用例必红；修复后必绿。
     */
    @Test
    void encryptDecryptAcrossDifferentWindowOffsetsRoundTrips() throws Exception {
        DataPackCodecOptions config = new NetPacketCodecSetting();
        config.setSecurityKeys(new String[]{"bench-encrypt-scope-key"});
        DataPackageContext packager = new DataPackageContext(999999L, config);

        byte[] message = BODY.getBytes(StandardCharsets.UTF_8);
        int len = message.length;

        // 参照组：窗口起点 0（历史侥幸正确形态）
        byte[] atZero = message.clone();
        new XOrCodecCrypto().encrypt(packager, atZero, 0, len);

        // 生产组：同一消息放进池页深处（arrayOffset=13），再跨"起点 0"的接收端解密
        byte[] page = new byte[len + 13];
        System.arraycopy(message, 0, page, 13, len);
        XOrCodecCrypto crypto = new XOrCodecCrypto();
        crypto.encrypt(packager, page, 13, len);
        byte[] wire = Arrays.copyOfRange(page, 13, 13 + len);
        assertArrayEquals(atZero, wire,
                "同一消息因两端缓冲池位置不同产生不同密文（绝对相位缺陷），或加密窗口被整段跳过（边界缺陷）");

        byte[] plain = wire.clone();
        crypto.decrypt(packager, plain, 0, len);
        assertArrayEquals(message, plain, "跨窗口起点往返必须还原原文");
    }

    @Test
    void tamperedCipherPayloadIsRejected() throws Exception {
        Fixture f = new Fixture(true);
        ByteBuf out = Unpooled.buffer(64 * 1024);
        f.encoder.encodeObject(f.ctx, f.request(BODY), out);

        // 载荷区中部篡改 1 字节（跳过帧头，避开头尾校验码）
        int mid = out.readerIndex() + 32;
        out.setByte(mid, out.getByte(mid) ^ 0x5A);

        NetCodecException thrown = assertThrows(NetCodecException.class,
                () -> f.decoder.decodeObject(f.ctx, out, new NetPacketDecodeMarker()));
        assertTrue(thrown.getMessage().toLowerCase().contains("verify"), "篡改密文必须触发校验失败");
        out.release();
    }

    /** 编解码夹具：verify=CRC64 + crypto=XOr + securityKeys（生产默认防线形态） */
    /**
     * 向后兼容锚（W-3 / net-protocol"未声明加密位的报文行为不变"）：
     * ENCRYPT 关闭的帧 MUST NOT 触达 crypto 路径——option 位清晰、载荷原样上线、往返不受修复影响。
     */
    @Test
    void framesWithoutEncryptBitRemainUntouched() throws Exception {
        Fixture f = new Fixture(false);
        ByteBuf out = Unpooled.buffer(64 * 1024);
        f.encoder.encodeObject(f.ctx, f.request(BODY), out);

        byte option = out.getByte(out.readerIndex() + 4); // 4 字节 magic 之后为 option
        assertFalse(CodecConstants.isOption(option, CodecConstants.DATA_PACK_OPTION_ENCRYPT),
                "未启用加密时 option 的 ENCRYPT 位必须为 0");
        byte[] frame = new byte[out.readableBytes()];
        out.getBytes(out.readerIndex(), frame);
        assertTrue(contains(frame, BODY.getBytes(StandardCharsets.UTF_8)),
                "未声明加密的帧必须原样携带载荷（该路径不经过 crypto，与修复前逐字节一致）");

        Message decoded = f.decoder.decodeObject(f.ctx, out, new NetPacketDecodeMarker());
        assertEquals(BODY, decoded.getBody(), "加密关闭的往返不得受 xor 修复影响");
        out.release();
    }

    private static boolean contains(byte[] haystack, byte[] needle) {
        outer:
        for (int i = 0; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

    private static final class Fixture {
        final EmbeddedChannel channel = new EmbeddedChannel();
        final NetTunnel tunnel = mock(NetTunnel.class);
        final ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        final NetPacketV1Encoder encoder;
        final NetPacketV1Decoder decoder;
        final CommonMessageFactory messageFactory = new CommonMessageFactory();

        Fixture(boolean encrypt) {
            NetPacketCodecSetting config = new NetPacketCodecSetting();
            config.setVerifyEnable(true);
            config.setEncryptEnable(encrypt);
            config.setWasteBytesEnable(false);
            config.setMaxPayloadLength(0xFFFF);
            config.setSecurityKeys(new String[]{"bench-encrypt-scope-key"});

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

            encoder = new NetPacketV1Encoder(config);
            encoder.setMessageCodec(messageCodec);
            encoder.verifier = new CRC64CodecVerifier();
            encoder.crypto = new XOrCodecCrypto();

            decoder = new NetPacketV1Decoder(config);
            decoder.setMessageCodec(messageCodec);
            decoder.verifier = new CRC64CodecVerifier();
            decoder.crypto = new XOrCodecCrypto();
        }

        Message request(String body) {
            return messageFactory.create(1L, MessageContents.push(Protocols.protocol(1000), body));
        }
    }
}
