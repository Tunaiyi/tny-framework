package com.tny.game.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.*;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.tny.game.codec.exception.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 12:48 下午
 */
public final class ProtobufCodecManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProtobufCodecManager.class);

    private static final ProtobufCodecManager INSTANCE = new ProtobufCodecManager();

    private static final Map<Class<?>, Codec<?>> typeSchemeMap = new ConcurrentHashMap<>();

    private ProtobufCodecManager() {
    }

    public static ProtobufCodecManager getInstance() {
        return INSTANCE;
    }

    private static <T> Codec<T> createCodec(Class<T> type) {
        ProtobufClass protobufClass = type.getAnnotation(ProtobufClass.class);
        Asserts.checkNotNull(protobufClass, "{} class annotation {} no exist",
                type, ProtobufClass.class);
        return ProtobufProxy.create(type);
    }

    public <T> Codec<T> loadCodec(Class<T> valueClass) {
        Codec<?> codec = typeSchemeMap.get(valueClass);
        if (codec != null) {
            return as(codec);
        }
        try {
            Codec<T> newCodec = createCodec(valueClass);
            Codec<?> old = typeSchemeMap.putIfAbsent(valueClass, newCodec);
            if (old != null) {
                return as(old);
            }
            LOGGER.info("ProtobufCodec Load [{}]  finish", valueClass);
            return newCodec;
        } catch (Throwable e) {
            throw new ObjectCodecException(format("Load {} class TypeProtobufScheme exception", valueClass), e);
        }
    }

}
