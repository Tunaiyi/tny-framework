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
        ByteBuffer byteBuffer = buffer.nioBuffer();
        try (ByteArrayInputStream buffInput = new ByteArrayInputStream(byteBuffer.array(), byteBuffer.position(), byteBuffer.remaining())) {
            return codec.decode(buffInput);
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
            ByteBuffer objectBuffer = objectBuf.nioBuffer();
            buffer.writeBytes(objectBuffer.array(), objectBuffer.arrayOffset(), size);
        } finally {
            objectBuf.release();
        }
    }

}
