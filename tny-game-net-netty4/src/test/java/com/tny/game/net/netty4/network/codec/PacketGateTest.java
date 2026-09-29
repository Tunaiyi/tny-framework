/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.application.*;
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

import java.nio.charset.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 报文编解码防线（net-protocol 规格）：校验判定 / 超限拒发 / 解码上下文防护 / 通道保持。
 * 装配模式沿用 CoderTest（EmbeddedChannel + mock ctx），修正其为可被 JUnit 执行的形态。
 */
class PacketGateTest {

    /** 合法报文经校验开关打开的编解码往返成功（当前 verify 条件反效应，应红） */
    @Test
    void legitimatePacketPassesVerification() throws Exception {
        Fixture f = new Fixture(true, 0xFFFF);
        ByteBuf out = Unpooled.buffer();
        f.encoder.encodeObject(f.ctx, f.request("hello-gate"), out);

        Message decoded = f.decoder.decodeObject(f.ctx, out, new NetPacketDecodeMarker());

        assertNotNull(decoded, "校验通过的合法报文应正常解出（当前实现将其误判失败，本断言应红）");
    }

    /** 篡改校验码字节的报文必须被拒（当前实现放行篡改包，应红） */
    @Test
    void tamperedPacketIsRejected() throws Exception {
        Fixture f = new Fixture(true, 0xFFFF);
        ByteBuf out = Unpooled.buffer();
        f.encoder.encodeObject(f.ctx, f.request("tamper-me"), out);
        int last = out.writerIndex() - 1; // 帧尾=校验码末字节
        out.setByte(last, out.getByte(last) ^ 0x5A);

        NetCodecException thrown = assertThrows(NetCodecException.class,
                () -> f.decoder.decodeObject(f.ctx, out, new NetPacketDecodeMarker()));
        assertTrue(thrown.getMessage().toLowerCase().contains("verify"), "拒绝原因应为校验失败");
    }

    /** 编码超限必须本地抛出（当前仅 warn 照发，应红） */
    @Test
    void oversizeMessageFailsLocallyOnEncode() {
        Fixture f = new Fixture(false, 32);
        ByteBuf out = Unpooled.buffer();
        NetPacketEncodeException thrown = assertThrows(NetPacketEncodeException.class,
                () -> f.encoder.encodeObject(f.ctx, f.request("x".repeat(500)), out),
                "超限报文编码应抛异常拒绝（当前实现只 warn 继续写出，本断言应红）");
        assertTrue(thrown.getMessage().contains("payload"), "异常信息应指明 payload 超限");
    }

    /** 隧道未就绪时解码必须给出可诊断异常而非 NPE（当前 NPE，应红） */
    @Test
    void decodeWithoutReadyTunnelFailsDiagnostically() throws Exception {
        Fixture f = new Fixture(false, 0xFFFF);
        ByteBuf out = Unpooled.buffer();
        f.encoder.encodeObject(f.ctx, f.request("no-tunnel"), out);
        f.channel.attr(NettyNetAttrKeys.TUNNEL).set(null); // 制造绑定失败窗口

        NetCodecException thrown = assertThrows(NetCodecException.class,
                () -> f.decoder.decodeObject(f.ctx, out, new NetPacketDecodeMarker()),
                "应抛可诊断解码异常（当前实现抛 NPE，本断言应红）");
        assertTrue(thrown.getMessage().toLowerCase().contains("tunnel"), "异常应指明会话通道未就绪");
    }

    /** 编码方向异常不得经 exceptionCaught 传播（传播即触达关闭路径；当前会传播，应红） */
    @Test
    void encodeExceptionIsNotPropagatedByHandler() throws Exception {
        NettyMessageHandler handler = new NettyMessageHandler(mock(NetworkContext.class));
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        ChannelPipeline pipeline = mock(ChannelPipeline.class);
        when(ctx.pipeline()).thenReturn(pipeline);
        when(pipeline.last()).thenReturn(mock(ChannelHandler.class));

        handler.exceptionCaught(ctx, new NetPacketEncodeException("fixture encode failure"));

        verify(ctx, never()).fireExceptionCaught(any(Throwable.class));
    }

    /** 反例：解码方向异常维持既有传播（最终由管道关闭连接——D3 语义保持，现应绿） */
    @Test
    void decodeExceptionStillPropagates() throws Exception {
        NettyMessageHandler handler = new NettyMessageHandler(mock(NetworkContext.class));
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        ChannelPipeline pipeline = mock(ChannelPipeline.class);
        when(ctx.pipeline()).thenReturn(pipeline);
        when(pipeline.last()).thenReturn(mock(ChannelHandler.class));

        handler.exceptionCaught(ctx, NetCodecException.causeDecodeError("fixture decode failure"));

        verify(ctx).fireExceptionCaught(any(Throwable.class));
    }

    /** 编解码夹具：同一 channel 挂写/读两个包序上下文与隧道 */
    private static final class Fixture {
        final EmbeddedChannel channel = new EmbeddedChannel();
        final NetTunnel tunnel = mock(NetTunnel.class);
        final ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        final NetPacketV1Encoder encoder;
        final NetPacketV1Decoder decoder;
        final CommonMessageFactory messageFactory = new CommonMessageFactory();

        Fixture(boolean verify, int maxPayload) {
            NetPacketCodecSetting config = new NetPacketCodecSetting();
            config.setVerifyEnable(verify);
            config.setMaxPayloadLength(maxPayload);
            config.setEncryptEnable(false);
            config.setWasteBytesEnable(false);

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
            encoder.verifier = verify ? new CRC64CodecVerifier() : new NoopCodecVerifier();
            encoder.crypto = new NoneCodecCrypto();

            decoder = new NetPacketV1Decoder(config);
            decoder.setMessageCodec(messageCodec);
            decoder.verifier = verify ? new CRC64CodecVerifier() : new NoopCodecVerifier();
            decoder.crypto = new NoneCodecCrypto();
        }

        Message request(String body) {
            return messageFactory.create(1L, MessageContents.push(Protocols.protocol(1000), body));
        }
    }

}
