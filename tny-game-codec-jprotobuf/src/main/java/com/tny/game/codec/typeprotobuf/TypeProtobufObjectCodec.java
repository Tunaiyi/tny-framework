package com.tny.game.codec.typeprotobuf;

import com.google.protobuf.*;
import com.tny.game.codec.*;
import com.tny.game.common.concurrent.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/9 5:56 下午
 */
public class TypeProtobufObjectCodec<T> implements ObjectCodec<T> {

    private final TypeProtobufSchemeManager schemeManager = TypeProtobufSchemeManager.getInstance();

    private static final ThreadLocalVar<ByteArrayOutputStream> DEFAULT_LOCAL_BUFF = new DefaultThreadLocalVar<>(
            () -> new ByteArrayOutputStream(16384));

    private final ThreadLocalVar<ByteArrayOutputStream> localBuffer;

    public TypeProtobufObjectCodec() {
        this(null);
    }

    public TypeProtobufObjectCodec(ThreadLocalVar<ByteArrayOutputStream> localBuffer) {
        if (localBuffer == null) {
            this.localBuffer = DEFAULT_LOCAL_BUFF;
        } else {
            this.localBuffer = localBuffer;
        }
    }

    @Override
    public boolean isPlaintext() {
        return false;
    }

    @Override
    public byte[] encode(T value) throws IOException {
        if (value == null) {
            return new byte[0];
        }
        Class<T> valueClass = as(value.getClass());
        TypeProtobufScheme<T> scheme = this.schemeManager.loadScheme(valueClass);
        ByteArrayOutputStream output = this.localBuffer.get();
        output.reset();
        CodedOutputStream out = CodedOutputStream.newInstance(output);
        out.writeFixed32NoTag(scheme.getId());
        scheme.getCodec().writeTo(value, out);
        out.flush();
        return output.toByteArray();
    }

    @Override
    public void encode(T value, OutputStream output) throws IOException {
        if (value == null) {
            return;
        }
        Class<T> valueClass = as(value.getClass());
        TypeProtobufScheme<T> scheme = this.schemeManager.loadScheme(valueClass);
        CodedOutputStream out = CodedOutputStream.newInstance(output);
        out.writeFixed32NoTag(scheme.getId());
        scheme.getCodec().writeTo(value, out);
        out.flush();
    }

    @Override
    public T decode(byte[] bytes) throws IOException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        CodedInputStream in = CodedInputStream.newInstance(input);
        int typeId = in.readFixed32();
        TypeProtobufScheme<T> scheme = this.schemeManager.getScheme(typeId);
        return scheme.getCodec().readFrom(in);
    }

    @Override
    public T decode(InputStream input) throws IOException {
        if (input.available() <= 0) {
            return null;
        }
        CodedInputStream in = CodedInputStream.newInstance(input);
        int typeId = in.readFixed32();
        TypeProtobufScheme<T> scheme = this.schemeManager.getScheme(typeId);
        return scheme.getCodec().readFrom(in);
    }

}
