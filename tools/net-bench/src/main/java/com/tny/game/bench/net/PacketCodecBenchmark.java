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
package com.tny.game.bench.net;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * 报文全管线编解码吞吐基线（任务 2.1）：编码→解码往返，verify 关闭/开启两参数化。
 * 装配复刻 PacketGateTest（生产 codec 形态），mock 仅用于 ChannelHandlerContext。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class PacketCodecBenchmark {

    @Param({"false", "true"})
    private boolean verify;

    private NetPacketV1Encoder encoder;
    private NetPacketV1Decoder decoder;
    private ChannelHandlerContext ctx;
    private NetPacketDecodeMarker marker;
    private Message message;
    private ByteBuf out;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        NetPacketCodecSetting config = new NetPacketCodecSetting();
        config.setVerifyEnable(verify);
        config.setEncryptEnable(false);
        config.setWasteBytesEnable(false);
        config.setSecurityKeys(new String[]{"bench-key"});

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

        EmbeddedChannel channel = new EmbeddedChannel();
        NetTunnel tunnel = mock(NetTunnel.class);
        when(tunnel.getAccessId()).thenReturn(999999L);
        when(tunnel.getMessageFactory()).thenReturn(new CommonMessageFactory());
        channel.attr(NettyNetAttrKeys.TUNNEL).set(tunnel);
        ctx = mock(ChannelHandlerContext.class);
        when(ctx.channel()).thenReturn(channel);

        encoder = new NetPacketV1Encoder(config);
        encoder.setMessageCodec(messageCodec);
        CodecVerifier codecVerifier = verify ? new CRC64CodecVerifier() : new NoopCodecVerifier();
        CodecCrypto codecCrypto = new NoneCodecCrypto();
        inject(encoder, codecVerifier, codecCrypto);

        decoder = new NetPacketV1Decoder(config);
        decoder.setMessageCodec(messageCodec);
        inject(decoder, codecVerifier, codecCrypto);

        marker = new NetPacketDecodeMarker();
        message = new CommonMessageFactory().create(1L,
                MessageContents.push(Protocols.protocol(1000), "bench-payload-of-average-size-96-bytes-0123456789abcdef"));
        out = Unpooled.buffer(4096);
    }

    /** verifier/crypto 为 NetPacketV1Codec protected 字段，bench 跨包经反射注入（等价同包测试装配） */
    private static void inject(NetPacketV1Codec codec, CodecVerifier verifier, CodecCrypto crypto) throws Exception {
        java.lang.reflect.Field vf = NetPacketV1Codec.class.getDeclaredField("verifier");
        vf.setAccessible(true);
        vf.set(codec, verifier);
        java.lang.reflect.Field cf = NetPacketV1Codec.class.getDeclaredField("crypto");
        cf.setAccessible(true);
        cf.set(codec, crypto);
    }

    @Benchmark
    public Object encodeThenDecode() throws Exception {
        out.clear();
        encoder.encodeObject(ctx, message, out);
        return decoder.decodeObject(ctx, out, marker);
    }

}
