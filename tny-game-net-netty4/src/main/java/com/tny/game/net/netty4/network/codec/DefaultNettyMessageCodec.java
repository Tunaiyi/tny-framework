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

import com.tny.game.common.utils.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.CodecConstants.*;
import static com.tny.game.net.message.MessageType.*;

public class DefaultNettyMessageCodec implements NettyMessageCodec {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultNettyMessageCodec.class);

    private final MessageBodyCodec<Object> messageBodyCodec;

    private final MessageHeaderCodec messageHeaderCodec;

    private final MessageRelayStrategy messageRelayStrategy;

    public DefaultNettyMessageCodec(MessageBodyCodec<?> messageBodyCodec, MessageHeaderCodec messageHeaderCodec) {
        this(messageBodyCodec, messageHeaderCodec, null);
    }

    public DefaultNettyMessageCodec(MessageBodyCodec<?> messageBodyCodec, MessageHeaderCodec messageHeaderCodec, MessageRelayStrategy relayStrategy) {
        this.messageBodyCodec = as(messageBodyCodec);
        this.messageHeaderCodec = messageHeaderCodec;
        this.messageRelayStrategy = ObjectAide.ifNull(relayStrategy, MessageRelayStrategy.NO_RELAY_STRATEGY);
    }

    @Override
    public NetMessage decode(ByteBuf buffer, MessageFactory messageFactory) throws Exception {
        if (!buffer.isReadable()) {
            return null;
        }
        long id = NettyVarIntCoder.readVarInt64(buffer);
        byte option = buffer.readByte();
        MessageMode mode = MessageMode.valueOf(MESSAGE, (byte) (option & MESSAGE_HEAD_OPTION_MODE_MASK));
        int protocol = NettyVarIntCoder.readVarInt32(buffer);
        int code = NettyVarIntCoder.readVarInt32(buffer);
        long toMessage = NettyVarIntCoder.readVarInt64(buffer);
        long time = NettyVarIntCoder.readVarInt64(buffer);
        int line = (byte) (option & MESSAGE_HEAD_OPTION_LINE_MASK);
        line = line >> MESSAGE_HEAD_OPTION_LINE_SHIFT;
        Map<String, MessageHeader<?>> headerMap = Collections.emptyMap();
        if (CodecConstants.isOption(option, MESSAGE_HEAD_OPTION_EXIST_HEADERS_VALUE_EXIST)) {
            headerMap = readHeaders(buffer);
        }
        CommonMessageHead head = new CommonMessageHead(id, mode, line, protocol, code, toMessage, time, headerMap);
        boolean relay = this.messageRelayStrategy.isRelay(head);
        NetMessage message;
        if (!CodecConstants.isOption(option, MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST)) {
            message = messageFactory.create(head, null);
        } else {
            Object body = readBody(buffer, relay);
            message = messageFactory.create(head, body);
        }
        message.relay(relay);
        return message;
    }

    @Override
    public void encode(NetMessage message, ByteBuf buffer) throws Exception {
        // TODO NetMessage 区分 TickMessage 与 ContentMessage！！
        NettyVarIntCoder.writeVarInt64(message.getId(), buffer);
        MessageHead head = message.getHead();
        MessageMode mode = head.getMode();
        boolean hasHeader = message.isHasHeaders();
        byte option = mode.getOption();
        option = (byte) (option |
                         (message.existBody() ? CodecConstants.MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST : (byte) 0) |
                         (hasHeader ? CodecConstants.MESSAGE_HEAD_OPTION_EXIST_HEADERS_VALUE_EXIST : (byte) 0));
        int line = head.getLine();
        if (line < MESSAGE_HEAD_OPTION_LINE_VALUE_MIN || line > MESSAGE_HEAD_OPTION_LINE_VALUE_MAX) {
            throw NetCodecException.causeEncodeFailed("line is {}. line must {} <= line <= {}", line,
                    MESSAGE_HEAD_OPTION_LINE_VALUE_MIN, MESSAGE_HEAD_OPTION_LINE_VALUE_MAX);
        }
        option = (byte) (option | line << MESSAGE_HEAD_OPTION_LINE_SHIFT);
        buffer.writeByte(option);
        NettyVarIntCoder.writeVarInt32(head.getProtocolId(), buffer);
        NettyVarIntCoder.writeVarInt32(head.getCode(), buffer);
        NettyVarIntCoder.writeVarInt64(head.getToMessage(), buffer);
        NettyVarIntCoder.writeVarInt64(head.getTime(), buffer);
        if (hasHeader) {
            writeHeaders(buffer, message.getAllHeaders());
        }
        if (message.existBody()) {
            writeBody(buffer, message.getBody());
        }
    }

    private Map<String, MessageHeader<?>> readHeaders(ByteBuf buffer) throws Exception {
        Map<String, MessageHeader<?>> headerMap = new HashMap<>();
        int size = NettyVarIntCoder.readVarInt32(buffer);
        // 帧内声明不可信：每个头至少占 1 字节，计数以当前窗口可读长度为上界
        if (size < 0 || size > buffer.readableBytes()) {
            throw NetCodecException.causeDecodeError("header count {} beyond readable {}", size, buffer.readableBytes());
        }
        for (int index = 0; index < size; index++) {
            MessageHeader<?> header;
            try {
                header = this.messageHeaderCodec.decode(buffer);
            } catch (NetCodecException e) {
                throw e;
            } catch (Throwable e) {
                // 单头失败即整帧失败：吞掉继续会造成声明计数与实际字节错位，污染后续体解析
                throw NetCodecException.causeDecodeError(e, "decode header {} failed", index);
            }
            if (header == null) {
                throw NetCodecException.causeDecodeError("header is null at index {}", index);
            }
            headerMap.put(header.getKey(), header);
        }
        return headerMap;
    }

    private Object readBody(ByteBuf buffer, boolean relay) throws Exception {
        int length = NettyVarIntCoder.readVarInt32(buffer);
        // 先校验后分配：帧层只限定整窗大小，窗口内声明的攻击者数据不得驱动越窗分配（未认证远程 DoS 防线）
        if (length < 0 || length > buffer.readableBytes()) {
            throw NetCodecException.causeDecodeError("body length {} beyond readable {}", length, buffer.readableBytes());
        }
        ByteBuf bodyBuff = buffer.alloc().heapBuffer(length);
        try {
            buffer.readBytes(bodyBuff, length);
            if (relay) {
                ByteBufMessageBody messageBody = new ByteBufMessageBody(bodyBuff);
                bodyBuff = null; // 不释放, 所有权移交 messageBody, 等待转发后释放
                return messageBody;
            }
            return this.messageBodyCodec.decode(bodyBuff);
        } finally {
            if (bodyBuff != null) {
                ReferenceCountUtil.release(bodyBuff);
            }
        }
    }

    private void writeHeaders(ByteBuf buffer, List<MessageHeader<?>> headers) throws Exception {
        NettyVarIntCoder.writeVarInt32(headers.size(), buffer);
        for (MessageHeader<?> header : headers) {
            try {
                messageHeaderCodec.encode(header, buffer);
            } catch (NetCodecException e) {
                throw e;
            } catch (Throwable e) {
                // 任一头失败即整帧失败：声明计数与实际编码数不一致会让对端按声明数错位解析
                throw NetCodecException.causeEncodeFailed(e, "encode header {} failed", header.getKey());
            }
        }
    }

    private void writeBody(ByteBuf buffer, Object object) throws Exception {
        OctetMessageBody releaseBody = null;
        try {
            if (object instanceof byte[]) {
                write(buffer, (byte[]) object);
            } else if (object instanceof ByteArrayMessageBody) {
                ByteArrayMessageBody arrayMessageBody = as(object);
                releaseBody = arrayMessageBody;
                byte[] data = arrayMessageBody.getBody();
                write(buffer, data);
            } else if (object instanceof ByteBufMessageBody messageBody) {
                releaseBody = messageBody;
                ByteBuf data = messageBody.getBody();
                if (data == null) {
                    throw NetCodecException.causeEncodeFailed("ByteBufMessageBody is released");
                }
                NettyVarIntCoder.writeVarInt32(data.readableBytes(), buffer);
                buffer.writeBytes(data);
            } else {
                ByteBuf bodyBuf = null;
                try {
                    bodyBuf = buffer.alloc().heapBuffer();
                    this.messageBodyCodec.encode(as(object), bodyBuf);
                    NettyVarIntCoder.writeVarInt32(bodyBuf.readableBytes(), buffer);
                    buffer.writeBytes(bodyBuf);
                } finally {
                    ReferenceCountUtil.release(bodyBuf);
                }
            }
        } finally {
            OctetMessageBody.release(releaseBody);
        }
    }

    private void write(ByteBuf buffer, byte[] data) {
        NettyVarIntCoder.writeVarInt32(data.length, buffer);
        buffer.writeBytes(data);
    }

}
