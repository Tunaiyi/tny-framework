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

import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import org.junit.jupiter.api.*;

import java.nio.channels.ClosedChannelException;
import java.util.*;

import static com.tny.game.net.message.CodecConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 2（Wave-A）红灯基线：编码失败 = 零字节上线 + 本次写回执失败；
 * 关连与否仅由 closeOnError / ResultLevel 决定（net-protocol"编码失败零字节与回执失败"契约）。
 * 对应任务 2.1。
 */
class NetPacketEncodeHandlerTest {

    /**
     * 模拟半截帧事故：先写 magic+option，再抛错——修复前 handler 吞异常，5 字节残帧照常上线且回执成功。
     */
    private static final class HalfFrameThenThrowEncoder implements NetPacketEncoder {

        private final RuntimeException failure;

        HalfFrameThenThrowEncoder(RuntimeException failure) {
            this.failure = failure;
        }

        @Override
        public void encodeObject(ChannelHandlerContext ctx, Message message, ByteBuf out) {
            out.writeBytes(FRAME_MAGIC);
            out.writeByte(0);
            throw failure;
        }
    }

    private static final class SuccessEncoder implements NetPacketEncoder {

        @Override
        public void encodeObject(ChannelHandlerContext ctx, Message message, ByteBuf out) {
            out.writeBytes(FRAME_MAGIC);
            out.writeByte(0);
            NettyVarIntCoder.writeFixed32(2, out);
            out.writeBytes(new byte[]{1, 2});
        }
    }

    private static CommonMessage sampleMessage() {
        CommonMessageHead head = new CommonMessageHead(7L, MessageMode.REQUEST, 0, 100, 0, 0L, System.currentTimeMillis(),
                Collections.emptyMap());
        return new CommonMessage(head, null);
    }

    private static EmbeddedChannel channelWith(NetPacketEncoder encoder, boolean closeOnError) {
        return new EmbeddedChannel(new NetPacketEncodeHandler(encoder, closeOnError));
    }

    private static void flushQuietly(EmbeddedChannel channel) {
        try {
            channel.flushOutbound();
        } catch (Exception e) {
            // 编码失败策略已关连时，flush 面对的是已关通道——回执断言继续有效
            if (!(e instanceof ClosedChannelException)) {
                throw new AssertionError(e);
            }
        }
    }

    private static Throwable rootCause(Throwable error) {
        Throwable current = error;
        while (current != null && current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    @Test
    @DisplayName("WARN 级编码失败：零字节上线、写回执失败、通道保持")
    void warnEncodeFailureZeroBytesReceiptFails() {
        EmbeddedChannel channel = channelWith(new HalfFrameThenThrowEncoder(
                NetCodecException.causeEncodeFailed("stub body codec boom")), false);

        // 用 pipeline().write + 独立 promise 模拟生产 writeAndFlush 路径（不预检开启状态、回执经 promise 完成）：
        // 修复前 handler 吞异常 → out 残帧被当作成功写出；修复后回执必须失败完成且原因为编码异常
        DefaultChannelPromise future = new DefaultChannelPromise(channel);
        channel.pipeline().write(sampleMessage(), future);
        flushQuietly(channel);

        assertTrue(future.isDone(), "嵌入式管线同步执行，回执必须即时完成");
        assertFalse(future.isSuccess(), "写回执必须以失败完成");
        assertNotNull(future.cause());
        assertTrue(rootCause(future.cause()).getMessage().contains("stub body codec boom"), "失败原因必须可读到");
        assertTrue(channel.outboundMessages().isEmpty(), "编码失败不得有任何字节上线（残帧防线）");
        assertTrue(channel.isOpen(), "WARN 级失败不关连接");
    }

    @Test
    @DisplayName("ERROR 级编码失败：写回执失败且按既有分级策略关连")
    void errorEncodeFailureClosesChannel() {
        EmbeddedChannel channel = channelWith(new HalfFrameThenThrowEncoder(
                NetCodecException.causeEncodeError("stub fatal boom")), false);

        // 用 pipeline().write + 独立 promise 模拟生产 writeAndFlush 路径（不预检开启状态、回执经 promise 完成）：
        // 修复前 handler 吞异常 → out 残帧被当作成功写出；修复后回执必须失败完成且原因为编码异常
        DefaultChannelPromise future = new DefaultChannelPromise(channel);
        channel.pipeline().write(sampleMessage(), future);
        flushQuietly(channel);

        assertTrue(future.isDone(), "回执必须在限时内完成");
        assertFalse(future.isSuccess());
        assertTrue(rootCause(future.cause()).getMessage().contains("stub fatal boom"),
                "回执失败原因必须是编码异常本身，而非关连的 ClosedChannelException");
        assertTrue(channel.outboundMessages().isEmpty(), "关连路径同样不得泄漏残帧");
        assertFalse(channel.isOpen(), "ERROR 级失败应关闭通道");
    }

    @Test
    @DisplayName("closeOnError=true 时 WARN 级失败也按配置关连")
    void closeOnErrorFlagHonored() {
        EmbeddedChannel channel = channelWith(new HalfFrameThenThrowEncoder(
                NetCodecException.causeEncodeFailed("stub body codec boom")), true);

        // 用 pipeline().write + 独立 promise 模拟生产 writeAndFlush 路径（不预检开启状态、回执经 promise 完成）：
        // 修复前 handler 吞异常 → out 残帧被当作成功写出；修复后回执必须失败完成且原因为编码异常
        DefaultChannelPromise future = new DefaultChannelPromise(channel);
        channel.pipeline().write(sampleMessage(), future);
        flushQuietly(channel);
        assertTrue(future.isDone(), "回执必须在限时内完成");
        assertFalse(future.isSuccess());
        assertTrue(rootCause(future.cause()).getMessage().contains("stub body codec boom"),
                "回执失败原因必须是编码异常本身");
        assertFalse(channel.isOpen());
    }

    @Test
    @DisplayName("兼容锚：编码成功的帧正常写出、回执成功、字节布局与修复前一致")
    void successPathUnchanged() {
        EmbeddedChannel channel = channelWith(new SuccessEncoder(), false);
        // 用 pipeline().write + 独立 promise 模拟生产 writeAndFlush 路径（不预检开启状态、回执经 promise 完成）：
        // 修复前 handler 吞异常 → out 残帧被当作成功写出；修复后回执必须失败完成且原因为编码异常
        DefaultChannelPromise future = new DefaultChannelPromise(channel);
        channel.pipeline().write(sampleMessage(), future);
        flushQuietly(channel);

        assertTrue(future.isDone(), "嵌入式管线同步执行，回执必须即时完成");
        assertTrue(future.isSuccess());
        assertEquals(1, channel.outboundMessages().size());
        ByteBuf written = (ByteBuf) channel.outboundMessages().peek();
        byte[] bytes = new byte[written.readableBytes()];
        written.getBytes(written.readerIndex(), bytes);
        byte[] expected = new byte[]{
                't', 'n', 'y', '.', 0,
                (byte) 2, (byte) 0, (byte) 0, (byte) 0,   // writeFixed32(2) little-endian
                1, 2};
        assertArrayEquals(expected, bytes, "成功路径帧字节必须逐位不变（wire 兼容）");
    }

    // ==================== writeHeaders 计数一致性（任务 2.3 红灯） ====================

    static final class TestHeader extends MessageHeader<TestHeader> {

        final String key;

        TestHeader(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public boolean isTransitive() {
            return false;
        }
    }

    private static final class ExplodingHeaderCodec implements MessageHeaderCodec {

        int encodeCalls;

        @Override
        public MessageHeader<?> decode(ByteBuf buffer) {
            throw new UnsupportedOperationException("encode-only stub");
        }

        @Override
        public void encode(MessageHeader<?> object, ByteBuf buffer) {
            if (++encodeCalls == 2) {
                throw new IllegalStateException("stub header boom at 2");
            }
            buffer.writeBytes(new byte[]{1, 2});
        }
    }

    private static final class NoopBodyCodec implements MessageBodyCodec<Object> {

        @Override
        public Object decode(ByteBuf buffer) {
            throw new UnsupportedOperationException("encode-only stub");
        }

        @Override
        public void encode(Object object, ByteBuf buffer) {
            // nothing to encode for this case (message has no body)
        }
    }

    @Test
    @DisplayName("头部编码部分失败：整帧编码失败（声明计数与实际必须一致）")
    void headerEncodeFailureFailsWholeFrame() {
        ExplodingHeaderCodec headerCodec = new ExplodingHeaderCodec();
        DefaultNettyMessageCodec codec = new DefaultNettyMessageCodec(new NoopBodyCodec(), headerCodec);
        Map<String, MessageHeader<?>> headers = new LinkedHashMap<>();
        headers.put("a", new TestHeader("a"));
        headers.put("b", new TestHeader("b"));
        CommonMessageHead head = new CommonMessageHead(9L, MessageMode.REQUEST, 0, 100, 0, 0L, 1L, headers);
        ByteBuf out = Unpooled.buffer();

        assertThrows(NetCodecException.class, () -> codec.encode(new CommonMessage(head, null), out));
    }

}
