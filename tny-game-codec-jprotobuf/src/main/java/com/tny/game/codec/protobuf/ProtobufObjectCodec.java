package com.tny.game.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.*;
import com.google.protobuf.*;
import com.tny.game.codec.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/9 5:56 下午
 */
public class ProtobufObjectCodec<T> implements ObjectCodec<T> {

    private final Codec<T> codec;

    public ProtobufObjectCodec(Class<T> type) {
        this.codec = ProtobufProxy.create(type);
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
        return this.codec.encode(value);
    }

    @Override
    public void encode(T value, OutputStream output) throws IOException {
        CodedOutputStream out = CodedOutputStream.newInstance(output);
        codec.writeTo(value, out);
        out.flush();
    }

    @Override
    public T decode(byte[] bytes) throws IOException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return this.codec.decode(bytes);
    }

    @Override
    public T decode(InputStream input) throws IOException {
        CodedInputStream in = CodedInputStream.newInstance(input);
        return codec.readFrom(in);
    }

}
