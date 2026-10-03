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
package com.tny.game.benchmark.net.routine;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.command.processor.CommandExecutor;
import com.tny.game.net.command.processor.CommandExecutorFactory;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.rpc.NetAccessMode;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.openjdk.jmh.annotations.*;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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
    private EmbeddedChannel channel;
    private BenchTunnel stubTunnel;
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

        // 方法论修正（allocation-profile 记录）：禁用 mockito——其拦截栈污染 per-op 分配归因；
        // ctx 取真实 pipeline context，tunnel 用手写零分配 stub
        // firstContext() 只返回 user handler 的 context（head/tail 不计）——须有占位 handler
        channel = new EmbeddedChannel(new io.netty.channel.ChannelDuplexHandler() {
        });
        stubTunnel = new BenchTunnel();
        channel.attr(NettyNetAttrKeys.TUNNEL).set(stubTunnel);
        ctx = channel.pipeline().firstContext();

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

    /** 零分配基准隧道桩：仅提供 codec 路径所需的最小面 */
    static final class BenchTunnel implements NetTunnel {
        private final CommonMessageFactory messageFactory = new CommonMessageFactory();
        private long accessId = 999999L;
        private NetSession session;

        @Override
        public long getAccessId() {
            return accessId;
        }

        @Override
        public void setAccessId(long accessId) {
            this.accessId = accessId;
        }

        @Override
        public MessageFactory getMessageFactory() {
            return messageFactory;
        }

        @Override
        public NetSession getSession() {
            return session;
        }

        @Override
        public boolean bind(NetSession session) {
            this.session = session;
            return true;
        }

        @Override
        public Certificate getCertificate() {
            return Certificates.anonymous();
        }

        @Override
        public boolean receive(NetMessage message) {
            return false;
        }

        @Override
        public MessageSent send(MessageContent content) {
            return null;
        }

        @Override
        public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) {
            return null;
        }

        @Override
        public MessageWriteFuture write(Message message, MessageWriteFuture promise) {
            return promise;
        }

        @Override
        public void ping() {
        }

        @Override
        public void pong() {
        }

        @Override
        public boolean open() {
            return true;
        }

        @Override
        public void disconnect() {
        }

        @Override
        public void reset() {
        }

        @Override
        public TunnelEventWatches events() {
            return null;
        }

        @Override
        public long getId() {
            return 1L;
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public NetAccessMode getAccessMode() {
            return NetAccessMode.SERVER;
        }

        @Override
        public TunnelStatus getStatus() {
            return TunnelStatus.OPEN;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public boolean close() {
            return true;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return new InetSocketAddress("127.0.0.1", 7100);
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return new InetSocketAddress("127.0.0.1", 7000);
        }

        @Override
        public com.tny.game.common.context.Attributes attributes() {
            return null; // 基准路径不触达属性容器
        }

        @Override
        public com.tny.game.net.application.NetworkContext getContext() {
            return new com.tny.game.net.application.NetBootstrapContext();
        }
    }

}
