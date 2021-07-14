package com.tny.game.net.message.codec;

import com.baidu.bjf.remoting.protobuf.*;
import org.apache.commons.lang3.ArrayUtils;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/9 5:56 下午
 */
public class ProtobufObjectCodecor<T> implements ProtobufCodec<T> {

    private final Codec<T> codec;
    private final Class<T> type;
    private final ProtobufType protobufType;

    public ProtobufObjectCodecor(Class<T> type) {
        this.codec = ProtobufProxy.create(type);
        this.type = type;
        this.protobufType = type.getAnnotation(ProtobufType.class);
    }

    @Override
    public int getTypeId() {
        return this.protobufType.value();
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public T decode(byte[] bytes) throws Exception {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return this.codec.decode(bytes);
    }

    @Override
    public byte[] encode(T object) throws Exception {
        if (object == null) {
            return new byte[0];
        }
        return this.codec.encode(object);
    }

}
