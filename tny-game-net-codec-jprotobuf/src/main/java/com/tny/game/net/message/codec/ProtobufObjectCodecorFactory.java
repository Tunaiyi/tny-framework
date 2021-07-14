package com.tny.game.net.message.codec;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.tny.game.common.utils.*;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/19 6:27 下午
 */
public class ProtobufObjectCodecorFactory {

    private final Map<Class<?>, ProtobufCodec<?>> typeCodecMap = new ConcurrentHashMap<>();

    private final Map<Integer, ProtobufCodec<?>> idCodecMap = new ConcurrentHashMap<>();

    private static final ProtobufObjectCodecorFactory DEFAULT = new ProtobufObjectCodecorFactory();

    public static ProtobufObjectCodecorFactory getDefault() {
        return DEFAULT;
    }

    public ProtobufObjectCodecorFactory() {
    }

    public <T> ProtobufCodec<T> getCodecor(Type type) {
        Class<?> valueClass = loadClassFrom(type);
        ProtobufCodec<?> codec = this.typeCodecMap.get(valueClass);
        if (codec != null) {
            return as(codec);
        }
        return as(createCodecor(valueClass));
    }

    public <T> ProtobufCodec<T> getCodecor(int typeId) {
        return as(this.idCodecMap.get(typeId));
    }

    private ProtobufCodec<?> createCodecor(Class<?> valueClass) {
        ThrowAide.checkNotNull(valueClass.getAnnotation(ProtobufType.class), "class {} is miss {} annotation", valueClass, ProtobufType.class);
        ThrowAide.checkNotNull(valueClass.getAnnotation(ProtobufClass.class), "class {} is miss {} annotation", valueClass, ProtobufClass.class);
        ProtobufCodec<?> codec = new ProtobufObjectCodecor<>(valueClass);
        ProtobufCodec<?> old = this.typeCodecMap.putIfAbsent(codec.getType(), codec);
        if (old != null) {
            return old;
        }
        old = this.idCodecMap.put(codec.getTypeId(), codec);
        if (old != null && old.getType() != codec.getType()) {
            throw new IllegalArgumentException(format("{} and {} are same ProtobufType id {}",
                    codec.getType(), old.getType(), old.getTypeId()));
        }
        return codec;
    }

    private Class<?> loadClassFrom(Type type) {
        Class<?> valueClass = null;
        if (type instanceof Class) {
            valueClass = (Class<?>)type;
        }
        if (type instanceof ParameterizedType) {
            valueClass = (Class<?>)((ParameterizedType)type).getRawType();
        }
        if (valueClass == null) {
            throw new IllegalArgumentException(format("unsupported type {}}", type));
        }
        return valueClass;
    }

}
