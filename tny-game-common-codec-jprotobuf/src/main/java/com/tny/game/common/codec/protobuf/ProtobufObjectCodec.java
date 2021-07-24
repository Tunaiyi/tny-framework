package com.tny.game.common.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.*;
import com.tny.game.common.codec.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

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
    public byte[] encodeToBytes(T value) throws IOException {
        if (value == null) {
            return new byte[0];
        }
        return this.codec.encode(value);
    }

    @Override
    public T decodeByBytes(byte[] bytes) throws IOException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return this.codec.decode(bytes);
    }

}
