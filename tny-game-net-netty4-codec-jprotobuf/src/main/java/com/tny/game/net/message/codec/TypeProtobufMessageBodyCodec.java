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

package com.tny.game.net.message.codec;

import com.tny.game.codec.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.session.*;
import com.tny.game.protoex.*;
import io.netty.buffer.*;
import io.netty.util.concurrent.FastThreadLocal;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.codec.ProtobufConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/11 11:13 下午
 */
@Unit
public class TypeProtobufMessageBodyCodec<T> implements MessageBodyCodec<T> {

    private final TypeProtobufObjectCodecFactory codecFactory;

    private final FastThreadLocal<MemoryAllotCounter> counterThreadLocal = new FastThreadLocal<>();

    public TypeProtobufMessageBodyCodec() {
        this.codecFactory = new TypeProtobufObjectCodecFactory();
    }

    public TypeProtobufMessageBodyCodec(TypeProtobufObjectCodecFactory codecFactory) {
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
    public T decode(ByteBuf buffer) throws Exception {
        try (ProtoExInputStream input = new ProtoExInputStream(buffer.nioBuffer())) {
            List<Object> paramList = null;
            Object body = null;
            while (input.hasRemaining()) {
                byte option = input.readByte();
                if ((option & PROTOBUF_MESSAGE_PARAMS) != 0 && paramList == null) {
                    paramList = new ArrayList<>();
                    body = new MessageParamList(paramList);
                }
                ProtobufRawType rawType = EnumAide.check(ProtobufRawType.class, (byte) (option & PROTOBUF_HEAD_TYPE_BIT_MASK));
                Object value = doDecode(rawType, input);
                if (paramList != null) {
                    paramList.add(value);
                } else {
                    body = value;
                }
            }
            return as(body);
        }
    }

    private Object doDecode(ProtobufRawType rawType, ProtoExInputStream input) throws Exception {
        if (rawType.isHasValueReader()) {
            return rawType.readerValue(input);
        } else {
            ObjectCodec<T> codec = this.codecFactory.createCodec(null);
            ByteBuffer buffer = input.readBuffer();
            try (ByteArrayInputStream buffInput = new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.remaining())) {
                return codec.decode(buffInput);
            }
        }
    }

    @Override
    public void encode(T object, ByteBuf buffer) throws Exception {
        try (ProtoExOutputStream output = new ProtoExOutputStream(new NettyByteBufferAllocator(buffer.alloc()))) {
            if (object instanceof MessageParamList) {
                for (Object param : ((MessageParamList) object)) {
                    doEncode(output, buffer.alloc(), param, true);
                }
            } else {
                doEncode(output, buffer.alloc(), object, false);
            }
            output.toBuffer(new ByteBufOutputStream(buffer));
        }
    }

    private void doEncode(ProtoExOutputStream output, ByteBufAllocator allocator, Object object, boolean params) throws Exception {
        byte option = params ? PROTOBUF_MESSAGE_PARAMS : 0;
        if (object == null) {
            output.writeByte((byte) (option | ProtobufRawType.NULL.option()));
            return;
        }
        ProtobufRawType rawType = ProtobufRawType.ofObject(object);
        output.writeByte((byte) (option | rawType.option()));
        if (rawType.isHasValueWriter()) {
            rawType.writeValue(output, object);
        } else {
            MemoryAllotCounter counter = this.counter();
            ObjectCodec<Object> codec = this.codecFactory.createCodec(null);
            ByteBuf objectBuf = allocator.heapBuffer(counter.allot());
            try (ByteBufOutputStream objectOutput = new ByteBufOutputStream(objectBuf)) {
                codec.encode(object, objectOutput);
                int size = objectBuf.readableBytes();
                counter.recode(size);
                ByteBuffer objectBuffer = objectBuf.nioBuffer();
                output.writeBytes(objectBuffer.array(), objectBuffer.arrayOffset(), size);
            } finally {
                objectBuf.release();
            }
        }
    }

}
