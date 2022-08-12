/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
        MessageMode mode = MessageMode.valueOf(MESSAGE, (byte)(option & MESSAGE_HEAD_OPTION_MODE_MASK));
        int protocol = NettyVarIntCoder.readVarInt32(buffer);
        int code = NettyVarIntCoder.readVarInt32(buffer);
        long toMessage = NettyVarIntCoder.readVarInt64(buffer);
        long time = NettyVarIntCoder.readVarInt64(buffer);
        RpcForwardHeader forwardHeader = null;
        int line = (byte)(option & MESSAGE_HEAD_OPTION_LINE_MASK);
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
        if (message.getType() != MESSAGE) {
            return;
        }
        //		ProtoExOutputStream stream = new ProtoExOutputStream(1024, 2 * 1024);
        NettyVarIntCoder.writeVarInt64(message.getId(), buffer);
        MessageHead head = message.getHead();
        MessageMode mode = head.getMode();
        boolean hasHeader = message.isHasHeaders();
        byte option = mode.getOption();
        option = (byte)(option |
                (message.existBody() ? CodecConstants.MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST : (byte)0) |
                (hasHeader ? CodecConstants.MESSAGE_HEAD_OPTION_EXIST_HEADERS_VALUE_EXIST : (byte)0));
        int line = head.getLine();
        if (line < MESSAGE_HEAD_OPTION_LINE_VALUE_MIN || line > MESSAGE_HEAD_OPTION_LINE_VALUE_MAX) {
            throw NetCodecException.causeEncodeFailed("line is {}. line must {} <= line <= {}", line,
                    MESSAGE_HEAD_OPTION_LINE_VALUE_MIN, MESSAGE_HEAD_OPTION_LINE_VALUE_MAX);
        }
        option = (byte)(option | line << MESSAGE_HEAD_OPTION_LINE_SHIFT);
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

    private <T> Map<String, MessageHeader<?>> readHeaders(ByteBuf buffer) {
        Map<String, MessageHeader<?>> headerMap = new HashMap<>();
        int size = NettyVarIntCoder.readVarInt32(buffer);
        for (int index = 0; index < size; index++) {
            try {
                MessageHeader<?> header = this.messageHeaderCodec.decode(buffer);
                if (header != null) {
                    headerMap.put(header.getKey(), header);
                }
            } catch (Throwable e) {
                LOGGER.warn("decode header exception", e);
            }
        }
        return headerMap;
    }

    private <T> Object readBody(ByteBuf buffer, boolean relay) throws Exception {
        Object body;
        int length = NettyVarIntCoder.readVarInt32(buffer);
        ByteBuf bodyBuff = buffer.alloc().heapBuffer(length);
        buffer.readBytes(bodyBuff, length);
        if (relay) {
            // 不释放, 等待转发后释放
            body = new ByteBufMessageBody(bodyBuff);
        } else {
            try {
                body = this.messageBodyCodec.decode(bodyBuff);
            } finally {
                ReferenceCountUtil.release(bodyBuff);
            }
        }
        return body;
    }

    private <T> void writeHeaders(ByteBuf buffer, List<MessageHeader<?>> headers) throws Exception {
        NettyVarIntCoder.writeVarInt32(headers.size(), buffer);
        for (MessageHeader<?> header : headers) {
            try {
                messageHeaderCodec.encode(header, buffer);
            } catch (Throwable e) {
                LOGGER.warn("encode header {} exception", header, e);
            }
        }
    }

    private <T> void writeBody(ByteBuf buffer, Object object) throws Exception {
        OctetMessageBody releaseBody = null;
        try {
            if (object instanceof byte[]) {
                write(buffer, (byte[])object);
            } else if (object instanceof ByteArrayMessageBody) {
                ByteArrayMessageBody arrayMessageBody = as(object);
                releaseBody = arrayMessageBody;
                byte[] data = arrayMessageBody.getBody();
                write(buffer, data);
            } else if (object instanceof ByteBufMessageBody) {
                ByteBufMessageBody messageBody = (ByteBufMessageBody)object;
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
