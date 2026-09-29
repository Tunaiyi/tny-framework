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

import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static com.tny.game.net.message.CodecConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 1（Wave-A）红灯基线：帧内长度声明的"先校验后分配"防线。
 * 覆盖 net-protocol delta：有界校验、头计数一致、心跳/配置不对称拒绝、旧版合法报文兼容锚。
 * 对应任务 1.1 / 1.4。
 */
class FrameLengthGuardTest {

    // ==================== 测试替身 ====================

    static final class StubBodyCodec implements MessageBodyCodec<Object> {

        final AtomicIntegerLike decodeCalls = new AtomicIntegerLike();

        @Override
        public Object decode(ByteBuf buffer) {
            decodeCalls.count++;
            byte[] data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);
            return data;
        }

        @Override
        public void encode(Object object, ByteBuf buffer) {
            byte[] data = (byte[]) object;
            NettyVarIntCoder.writeVarInt32(data.length, buffer);
            buffer.writeBytes(data);
        }
    }

    static final class StubHeaderCodec implements MessageHeaderCodec {

        final AtomicIntegerLike decodeCalls = new AtomicIntegerLike();
        int throwAtCall = -1;

        @Override
        public MessageHeader<?> decode(ByteBuf buffer) throws Exception {
            int call = ++decodeCalls.count;
            if (call == throwAtCall) {
                throw new IllegalStateException("stub header codec broken at call " + call);
            }
            int length = NettyVarIntCoder.readVarInt32(buffer);
            if (length < 0 || length > buffer.readableBytes()) {
                throw new EOFException("stub: declared header length " + length + " beyond readable");
            }
            byte[] value = new byte[length];
            buffer.readBytes(value);
            return new TestHeader("header-" + call, value);
        }

        @Override
        public void encode(MessageHeader<?> object, ByteBuf buffer) {
            byte[] value = ((TestHeader) object).value;
            NettyVarIntCoder.writeVarInt32(value.length, buffer);
            buffer.writeBytes(value);
        }
    }

    static final class TestHeader extends MessageHeader<TestHeader> {

        final String key;
        final byte[] value;

        TestHeader(String key, byte[] value) {
            this.key = key;
            this.value = value;
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

    static final class StubMessageFactory implements MessageFactory {

        @Override
        public NetMessage create(long id, MessageSubject subject) {
            throw new UnsupportedOperationException("not used");
        }

        @Override
        public NetMessage create(NetMessageHead head, Object body) {
            return new CommonMessage(head, body);
        }
    }

    static final class StubNettyMessageCodec implements NettyMessageCodec {

        final AtomicIntegerLike decodeCalls = new AtomicIntegerLike();

        @Override
        public NetMessage decode(ByteBuf bytes, MessageFactory factory) {
            decodeCalls.count++;
            return null;
        }

        @Override
        public void encode(NetMessage message, ByteBuf buffer) {
            // frame-level tests never exercise message body encoding
        }
    }

    static final class AtomicIntegerLike {
        int count;
    }

    // ==================== 消息级：DefaultNettyMessageCodec ====================

    private static ByteBuf messageHead(long id, byte option, int protocol) {
        ByteBuf buf = Unpooled.buffer();
        NettyVarIntCoder.writeVarInt64(id, buf);
        buf.writeByte(option);
        NettyVarIntCoder.writeVarInt32(protocol, buf);
        NettyVarIntCoder.writeVarInt32(0, buf);
        NettyVarIntCoder.writeVarInt64(0L, buf);
        NettyVarIntCoder.writeVarInt64(System.currentTimeMillis(), buf);
        return buf;
    }

    private DefaultNettyMessageCodec newMessageCodec(StubBodyCodec bodyCodec, StubHeaderCodec headerCodec) {
        return new DefaultNettyMessageCodec(bodyCodec, headerCodec, MessageRelayStrategy.NO_RELAY_STRATEGY);
    }

    @Test
    @DisplayName("体长度声明超过可读：以解码异常终结，不越界分配")
    void bodyLengthBeyondReadableRejected() {
        StubBodyCodec bodyCodec = new StubBodyCodec();
        DefaultNettyMessageCodec codec = newMessageCodec(bodyCodec, new StubHeaderCodec());
        byte option = (byte) (MessageMode.REQUEST.getOption() | MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST);
        ByteBuf buf = messageHead(1L, option, 100);
        NettyVarIntCoder.writeVarInt32(16 * 1024 * 1024, buf); // 声明 16MB，实际可读个位数
        buf.writeBytes(new byte[]{1, 2, 3});

        NetCodecException error = assertThrows(NetCodecException.class, () -> codec.decode(buf, new StubMessageFactory()));
        assertTrue(error.getMessage().contains("body"), "应指明体长度越界: " + error.getMessage());
        assertEquals(0, bodyCodec.decodeCalls.count, "越界声明不得进入体解码，更不得先分配再失败");
    }

    @Test
    @DisplayName("头声明计数多于实际：整帧失败而非静默丢头")
    void headerCountMismatchFailsFrame() {
        StubBodyCodec bodyCodec = new StubBodyCodec();
        StubHeaderCodec headerCodec = new StubHeaderCodec();
        DefaultNettyMessageCodec codec = newMessageCodec(bodyCodec, headerCodec);
        byte option = (byte) (MessageMode.REQUEST.getOption() | MESSAGE_HEAD_OPTION_EXIST_HEADERS_VALUE_EXIST
                              | MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST);
        ByteBuf buf = messageHead(2L, option, 100);
        NettyVarIntCoder.writeVarInt32(3, buf); // 声明 3 个头
        headerCodec.encode(new TestHeader("a", new byte[]{7}), buf); // 实际只有 1 个
        NettyVarIntCoder.writeVarInt32(2, buf);                      // 其后的"合法体"字节
        buf.writeBytes(new byte[]{9, 9});

        assertThrows(NetCodecException.class, () -> codec.decode(buf, new StubMessageFactory()));
        assertTrue(headerCodec.decodeCalls.count >= 2, "失败的声明头必须终止整帧");
        assertEquals(0, bodyCodec.decodeCalls.count, "头计数错位时不得继续解码体");
    }

    @Test
    @DisplayName("单头解码异常：整帧失败且不吞掉继续")
    void headerDecodeFailureAbortsFrame() {
        StubBodyCodec bodyCodec = new StubBodyCodec();
        StubHeaderCodec headerCodec = new StubHeaderCodec();
        headerCodec.throwAtCall = 1;
        DefaultNettyMessageCodec codec = newMessageCodec(bodyCodec, headerCodec);
        byte option = (byte) (MessageMode.REQUEST.getOption() | MESSAGE_HEAD_OPTION_EXIST_HEADERS_VALUE_EXIST);
        ByteBuf buf = messageHead(3L, option, 100);
        NettyVarIntCoder.writeVarInt32(1, buf);
        buf.writeBytes(new byte[]{5});

        NetCodecException error = assertThrows(NetCodecException.class, () -> codec.decode(buf, new StubMessageFactory()));
        assertEquals(0, bodyCodec.decodeCalls.count);
        assertTrue(error.getCause() instanceof IllegalStateException, "应保留原始头解码原因");
    }

    @Test
    @DisplayName("兼容锚：旧版合法报文（计数相符的头+体）正常解析、往返字节一致")
    @SuppressWarnings("unchecked")
    void legacyValidFrameStillDecodes() throws Exception {
        StubBodyCodec bodyCodec = new StubBodyCodec();
        StubHeaderCodec headerCodec = new StubHeaderCodec();
        DefaultNettyMessageCodec codec = newMessageCodec(bodyCodec, headerCodec);
        Map<String, MessageHeader<?>> headers = new LinkedHashMap<>();
        headers.put("a", new TestHeader("a", new byte[]{7}));
        headers.put("b", new TestHeader("b", new byte[]{8}));
        CommonMessageHead head = new CommonMessageHead(42L, MessageMode.REQUEST, 0, 100, 0, 0L, 12345L, headers);
        ByteBuf encoded = Unpooled.buffer();
        codec.encode(new CommonMessage(head, new byte[]{1, 2, 3}), encoded);

        NetMessage decoded = codec.decode(encoded, new StubMessageFactory());
        assertNotNull(decoded);
        assertEquals(42L, decoded.getId());
        assertEquals(100, decoded.getHead().getProtocolId());
        assertEquals(2, decoded.getAllHeaders().size(), "头计数声明与实际一致时不得丢头");
        assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) decoded.getBody());
    }

    @Test
    @DisplayName("头部对象编解码器：声明长度超可读时分配前拒绝")
    void headerCodecLengthGuard() throws Exception {
        TypeProtobufObjectCodecFactory fakeFactory = new TypeProtobufObjectCodecFactory() {
            @Override
            public <T> com.tny.game.codec.ObjectCodec<T> createCodec(Type type) {
                return new NopObjectCodec<>();
            }
        };
        DefaultMessageHeaderCodec headerCodec = new DefaultMessageHeaderCodec(fakeFactory);
        ByteBuf buf = Unpooled.buffer();
        NettyVarIntCoder.writeVarInt32(1000, buf);
        buf.writeBytes(new byte[10]);

        assertThrows(NetCodecException.class, () -> headerCodec.decode(buf));
    }

    static final class NopObjectCodec<T> implements com.tny.game.codec.ObjectCodec<T> {

        @Override
        public boolean isPlaintext() {
            return false;
        }

        @Override
        public byte[] encode(T value) {
            return new byte[0];
        }

        @Override
        public void encode(T value, OutputStream output) {
        }

        @Override
        @SuppressWarnings("unchecked")
        public T decode(byte[] bytes) {
            return (T) new TestHeader("nop", bytes);
        }

        @Override
        @SuppressWarnings("unchecked")
        public T decode(InputStream input) {
            return (T) new TestHeader("nop", new byte[0]);
        }
    }

    // ==================== 帧级：NetPacketV1Decoder ====================

    private static final byte[] MAGIC = FRAME_MAGIC;

    private NetPacketV1Decoder newPacketDecoder() {
        NetPacketCodecSetting options = new NetPacketCodecSetting(); // 真实默认：verify/encrypt/waste 均关闭，maxPayload 0xFFFF
        NetPacketV1Decoder decoder = new NetPacketV1Decoder(options);
        decoder.setMessageCodec(new StubNettyMessageCodec());
        decoder.setVerifier(mock(CodecVerifier.class));
        decoder.setCrypto(mock(CodecCrypto.class));
        return decoder;
    }

    private ChannelHandlerContext channelWithTunnel() {
        EmbeddedChannel channel = new EmbeddedChannel();
        NetTunnel tunnel = mock(NetTunnel.class);
        when(tunnel.getMessageFactory()).thenReturn(new StubMessageFactory());
        channel.attr(NettyNetAttrKeys.TUNNEL).set(tunnel);
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.channel()).thenReturn(channel);
        return ctx;
    }

    private static ByteBuf packetFrame(byte option, int payloadLength, byte[] payload, byte[] tail) {
        ByteBuf in = Unpooled.buffer();
        in.writeBytes(MAGIC);
        in.writeByte(option);
        NettyVarIntCoder.writeFixed32(payloadLength, in);
        in.writeBytes(payload);
        if (tail != null) {
            in.writeBytes(tail);
        }
        return in;
    }

    private static byte[] payloadHead(long accessId, int number) {
        ByteBuf p = Unpooled.buffer();
        NettyVarIntCoder.writeVarInt64(accessId, p);
        NettyVarIntCoder.writeVarInt32(number, p);
        byte[] head = new byte[p.readableBytes()];
        p.readBytes(head);
        p.release();
        return head;
    }

    @Test
    @DisplayName("payloadLength 为负：解码异常终结而非运行时崩溃")
    void negativePayloadLengthRejected() {
        NetPacketV1Decoder decoder = newPacketDecoder();
        byte[] head = payloadHead(5L, 1);
        byte[] payload = new byte[head.length + 2];
        System.arraycopy(head, 0, payload, 0, head.length);
        ByteBuf in = packetFrame(DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_MESSAGE, -5, payload, new byte[4]);

        NetCodecException error = assertThrows(NetCodecException.class, () -> decoder.decodeObject(channelWithTunnel(), in, new NetPacketDecodeMarker()));
        assertTrue(error.getMessage().contains("payloadLength"), "应指明 payloadLength 非法: " + error.getMessage());
    }

    @Test
    @DisplayName("payloadLength 小于头消耗（体长为负）：解码异常终结")
    void consumedHeadExceedsPayloadLengthRejected() {
        NetPacketV1Decoder decoder = newPacketDecoder();
        byte[] head = payloadHead(100000L, 1); // accessId 3B + number 1B = 4B
        ByteBuf in = packetFrame(DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_MESSAGE, 1, head, new byte[4]);

        assertThrows(NetCodecException.class, () -> decoder.decodeObject(channelWithTunnel(), in, new NetPacketDecodeMarker()));
    }

    @Test
    @DisplayName("配置关闭但报文声明废字节：显式拒绝而非错位解码")
    void wasteOptionConfigMismatchRejected() {
        NetPacketV1Decoder decoder = newPacketDecoder();
        byte[] head = payloadHead(5L, 1);
        byte[] payload = new byte[head.length + 4];
        System.arraycopy(head, 0, payload, 0, head.length);
        ByteBuf in = packetFrame(DATA_PACK_OPTION_WASTE_BYTES, payload.length, payload, null);

        NetCodecException error = assertThrows(NetCodecException.class, () -> decoder.decodeObject(channelWithTunnel(), in, new NetPacketDecodeMarker()));
        assertTrue(error.getMessage().contains("waste"), "应指明废字节不对称: " + error.getMessage());
    }

    @Test
    @DisplayName("配置关闭但报文声明校验位：显式拒绝")
    void verifyOptionConfigMismatchRejected() {
        CodecVerifier verifier = mock(CodecVerifier.class);
        when(verifier.getCodeLength()).thenReturn(8);
        when(verifier.verify(any(), any(), anyInt(), anyInt(), any())).thenReturn(true);
        NetPacketV1Decoder decoder = newPacketDecoder();
        decoder.setVerifier(verifier);
        byte[] head = payloadHead(5L, 1);
        byte[] payload = new byte[head.length + 16];
        System.arraycopy(head, 0, payload, 0, head.length);
        ByteBuf in = packetFrame(DATA_PACK_OPTION_VERIFY, payload.length, payload, null);

        assertThrows(NetCodecException.class, () -> decoder.decodeObject(channelWithTunnel(), in, new NetPacketDecodeMarker()));
    }

    @Test
    @DisplayName("配置关闭但报文声明加密位：显式拒绝")
    void encryptOptionConfigMismatchRejected() {
        NetPacketV1Decoder decoder = newPacketDecoder();
        byte[] head = payloadHead(5L, 1);
        byte[] payload = new byte[head.length + 8];
        System.arraycopy(head, 0, payload, 0, head.length);
        ByteBuf in = packetFrame(DATA_PACK_OPTION_ENCRYPT, payload.length, payload, null);

        assertThrows(NetCodecException.class, () -> decoder.decodeObject(channelWithTunnel(), in, new NetPacketDecodeMarker()));
    }

    @Test
    @DisplayName("兼容锚：合法帧（长度自洽、选项对称）正常进入消息解码")
    void validFrameStillDecodes() throws Exception {
        StubNettyMessageCodec messageCodec = new StubNettyMessageCodec();
        NetPacketV1Decoder decoder = newPacketDecoder();
        decoder.setMessageCodec(messageCodec);
        byte[] head = payloadHead(5L, 1);
        byte[] body = {1, 2, 3, 4};
        byte[] payload = new byte[head.length + body.length];
        System.arraycopy(head, 0, payload, 0, head.length);
        System.arraycopy(body, 0, payload, head.length, body.length);
        ByteBuf in = packetFrame(DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_MESSAGE, payload.length, payload, null);

        decoder.decodeObject(channelWithTunnel(), in, new NetPacketDecodeMarker());
        assertEquals(1, messageCodec.decodeCalls.count);
    }

}
