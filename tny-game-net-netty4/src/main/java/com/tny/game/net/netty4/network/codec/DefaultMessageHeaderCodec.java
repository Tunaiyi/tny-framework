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

import com.tny.game.codec.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;
import io.netty.buffer.*;
import io.netty.util.concurrent.FastThreadLocal;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/11 11:13 下午
 */
@Unit
public class DefaultMessageHeaderCodec implements MessageHeaderCodec {

    private final TypeProtobufObjectCodecFactory codecFactory;

    private final FastThreadLocal<MemoryAllotCounter> counterThreadLocal = new FastThreadLocal<>();

    static {
        TypeProtobufSchemeManager manager = TypeProtobufSchemeManager.getInstance();
        manager.loadScheme(RpcForwardHeader.class);
        manager.loadScheme(RpcOriginalMessageIdHeader.class);
    }

    public DefaultMessageHeaderCodec() {
        this.codecFactory = new TypeProtobufObjectCodecFactory();
    }

    public DefaultMessageHeaderCodec(TypeProtobufObjectCodecFactory codecFactory) {
        this.codecFactory = codecFactory;
    }

    private MemoryAllotCounter counter() {
        MemoryAllotCounter counter = counterThreadLocal.get();
        if (counter == null) {
            counterThreadLocal.set(counter = new MemoryAllotCounter());
        }
        return counter;
    }

    @Override
    public MessageHeader<?> decode(ByteBuf buffer) throws Exception {
        ObjectCodec<MessageHeader<?>> codec = this.codecFactory.createCodec(null);
        int length = NettyVarIntCoder.readVarInt32(buffer);
        ByteBuf headersBuf = buffer.alloc().heapBuffer(length);
        try {
            buffer.readBytes(headersBuf, length);
            ByteBuffer byteBuffer = headersBuf.nioBuffer();
            try (ByteArrayInputStream buffInput = new ByteArrayInputStream(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.remaining())) {
                return codec.decode(buffInput);
            }
        } finally {
            headersBuf.release();
        }
    }

    @Override
    public void encode(MessageHeader<?> object, ByteBuf buffer) throws Exception {
        MemoryAllotCounter counter = this.counter();
        ObjectCodec<Object> codec = this.codecFactory.createCodec(null);
        ByteBuf objectBuf = buffer.alloc().heapBuffer(counter.allot());
        try (ByteBufOutputStream objectOutput = new ByteBufOutputStream(objectBuf)) {
            codec.encode(object, objectOutput);
            int size = objectBuf.readableBytes();
            counter.recode(size);
            NettyVarIntCoder.writeVarInt32(size, buffer);
            ByteBuffer objectBuffer = objectBuf.nioBuffer();
            buffer.writeBytes(objectBuffer.array(), objectBuffer.arrayOffset(), size);
        } finally {
            objectBuf.release();
        }
    }

}
