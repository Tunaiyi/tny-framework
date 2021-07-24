package com.tny.game.common.codec.typeprotobuf;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.codec.*;
import com.tny.game.common.collection.*;
import com.tny.game.common.concurrent.*;
import org.springframework.util.MimeType;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Collection;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/19 6:27 下午
 */
public class TypeProtobufObjectCodecFactory extends AbstractObjectCodecFactory {

    private static final Collection<MimeType> DEFAULT_MIME_TYPES = ImmutableList
            .copyOf(MimeTypeAide.asList(TypeProtobufMimeType.TYPE_PROTOBUF, TypeProtobufMimeType.TYPE_PROTOBUF_WILDCARD));

    private TypeProtobufObjectCodec<?> codec;

    public TypeProtobufObjectCodecFactory() {
        this(null, null);
    }

    public TypeProtobufObjectCodecFactory(Collection<MimeType> supportMimeTypes) {
        this(supportMimeTypes, null);
    }

    public TypeProtobufObjectCodecFactory(ThreadLocalVar<ByteArrayOutputStream> localBuffer) {
        this(null, localBuffer);
    }

    public TypeProtobufObjectCodecFactory(Collection<MimeType> supportMimeTypes, ThreadLocalVar<ByteArrayOutputStream> localBuffer) {
        super(CollectionAide.ifEmpty(supportMimeTypes, DEFAULT_MIME_TYPES));
        if (localBuffer == null) {
            this.codec = new TypeProtobufObjectCodec<>();
        } else {
            this.codec = new TypeProtobufObjectCodec<>(localBuffer);
        }
    }

    @Override
    public <T> ObjectCodec<T> createCodecor(Type type) {
        return as(this.codec);
    }

}
