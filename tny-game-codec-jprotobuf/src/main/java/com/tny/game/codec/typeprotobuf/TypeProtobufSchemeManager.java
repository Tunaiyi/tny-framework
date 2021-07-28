package com.tny.game.codec.typeprotobuf;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.tny.game.codec.typeprotobuf.annotation.*;
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
public final class TypeProtobufSchemeManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(TypeProtobufSchemeManager.class);

    private static final TypeProtobufSchemeManager INSTANCE = new TypeProtobufSchemeManager();

    private static final Map<Integer, TypeProtobufScheme<?>> idSchemeMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, TypeProtobufScheme<?>> typeSchemeMap = new ConcurrentHashMap<>();

    private TypeProtobufSchemeManager() {
    }

    public static TypeProtobufSchemeManager getInstance() {
        return INSTANCE;
    }

    public <T> TypeProtobufScheme<T> getScheme(int id) {
        TypeProtobufScheme<T> scheme = as(idSchemeMap.get(id));
        return Asserts.checkNotNull(scheme, "TypeProtobuf Id {} TypeProtobufScheme is no exist");
    }

    public <T> TypeProtobufScheme<T> loadScheme(Class<T> valueClass) {
        Asserts.checkNotNull(valueClass.getAnnotation(TypeProtobuf.class), "class {} is miss {} annotation", valueClass, TypeProtobuf.class);
        Asserts.checkNotNull(valueClass.getAnnotation(ProtobufClass.class), "class {} is miss {} annotation", valueClass, ProtobufClass.class);
        TypeProtobufScheme<T> scheme = new TypeProtobufScheme<>(valueClass);
        TypeProtobufScheme<?> old = typeSchemeMap.putIfAbsent(scheme.getType(), scheme);
        if (old != null) {
            return as(old);
        }
        old = idSchemeMap.put(scheme.getId(), scheme);
        if (old != null && old.getType() != scheme.getType()) {
            throw new IllegalArgumentException(format("{} and {} are same ProtobufType id {}",
                    scheme.getType(), old.getType(), old.getId()));
        }
        LOGGER.info("TypeProtobufScheme Load [({}) {}]  finish", scheme.getId(), valueClass);
        return scheme;

    }

}
