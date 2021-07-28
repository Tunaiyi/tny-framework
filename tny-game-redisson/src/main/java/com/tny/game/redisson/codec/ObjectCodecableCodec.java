package com.tny.game.redisson.codec;

import com.tny.game.codec.*;
import io.netty.buffer.*;
import org.apache.commons.lang3.ClassUtils;
import org.redisson.client.codec.Codec;
import org.redisson.client.protocol.*;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/12 8:19 下午
 */
public class ObjectCodecableCodec extends TypedBaseCodec {

    private final ObjectCodec<Object> codecor;

    public ObjectCodecableCodec(Type type, ObjectCodec<Object> codecor) {
        super(type, null, false);
        this.codecor = codecor;
    }

    public ObjectCodecableCodec(Type type, String mimeType, ObjectCodecService objectCodecService) {
        this(type, mimeType, objectCodecService, null);
    }

    public ObjectCodecableCodec(Type type, String mimeType, ObjectCodecService objectCodecService, Codec keyCodec) {
        super(type, keyCodec, false);
        this.codecor = objectCodecService.codec(type, mimeType);
        this.initCodec();
    }

    @Override
    protected Decoder<Object> createDecoder(Type type) {
        return (buf, state) -> this.codecor.decodeByBytes(ByteBufUtil.getBytes(buf));
    }

    @Override
    protected Encoder createEncoder(Type type) {
        return in -> {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try (ByteBufOutputStream os = new ByteBufOutputStream(out)) {
                if (in instanceof byte[]) {
                    os.write((byte[])in);
                } else if (ClassUtils.isPrimitiveOrWrapper(in.getClass()) || in instanceof CharSequence) {
                    os.write(in.toString().getBytes(CoderCharsets.DEFAULT));
                } else {
                    os.write(this.codecor.encodeToBytes(in));
                }
                return os.buffer();
            } catch (IOException e) {
                out.release();
                throw e;
            }
        };
    }

    public static void main(String[] args) {
        System.out.println(MimeTypeUtils.parseMimeType("json"));
    }

}
