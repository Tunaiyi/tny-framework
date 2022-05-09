package com.tny.game.redisson;

import com.tny.game.codec.*;
import com.tny.game.redisson.codec.*;
import com.tny.game.redisson.exception.*;
import com.tny.game.redisson.script.*;
import org.apache.commons.lang3.ClassUtils;
import org.redisson.api.*;
import org.redisson.api.RScript.*;
import org.redisson.client.codec.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.codec.CoderCharsets.*;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/7 10:08 上午
 */
public class RedissonLuaScripRunner implements LuaScriptRunner {

    private static final StringCodec STRING_CODEC = new StringCodec(DEFAULT);

    private static final Map<Type, Codec> CODEC_MAP = new ConcurrentHashMap<>();

    private final RedissonClient redissonClient;

    private final ObjectCodecService objectCodecService;

    public RedissonLuaScripRunner(RedissonClient redissonClient, ObjectCodecService objectCodecService) {
        this.redissonClient = redissonClient;
        this.objectCodecService = objectCodecService;
    }

    private Class<?> loadClass(Type type) {
        if (type instanceof ParameterizedType) {
            return as(((ParameterizedType)type).getRawType());
        }
        return as(type);
    }

    @Override
    public <R> R run(LuaScript<?, R> script) {
        Class<?> returnClass = loadClass(script.getReturnType());
        ReturnType returnType = toReturnType(returnClass);
        try {
            Codec codec = getCodec(script.getElementType());
            RScript rScript;
            if (codec == null) {
                rScript = this.redissonClient.getScript();
            } else {
                rScript = this.redissonClient.getScript(codec);
            }
            return rScript.eval(Mode.READ_WRITE, script.getScript(), returnType,
                    as(script.getKeys()), encodeArgs(script.getArguments()));
        } catch (IOException e) {
            throw new LuaScripRunException("run script exception | {} ", script.getScript());
        }
    }

    private ReturnType toReturnType(Class<?> returnClass) {
        ReturnType returnType;
        if (ClassUtils.isPrimitiveOrWrapper(returnClass)) {
            returnClass = ClassUtils.primitiveToWrapper(returnClass);
            if (Integer.class.isAssignableFrom(returnClass) || Long.class.isAssignableFrom(returnClass)) {
                returnType = ReturnType.INTEGER;
            } else if (Boolean.class == returnClass) {
                returnType = ReturnType.BOOLEAN;
            } else {
                returnType = ReturnType.VALUE;
            }
        } else if (Collection.class.isAssignableFrom(returnClass)) {
            returnType = ReturnType.MULTI;
        } else if (Map.class.isAssignableFrom(returnClass)) {
            returnType = ReturnType.VALUE;
        } else {
            returnType = ReturnType.VALUE;
        }
        return returnType;
    }

    private Codec getCodec(Type elementType) {
        if (elementType == String.class) {
            return STRING_CODEC;
        }
        if (elementType instanceof Class && ClassUtils.isPrimitiveOrWrapper(as(elementType))) {
            Class<?> wrapperClass = ClassUtils.primitiveToWrapper((Class<?>)elementType);
            if (wrapperClass == Boolean.class) {
                return StringCodec.INSTANCE;
            }
            if (wrapperClass == Integer.class) {
                return IntegerCodec.INSTANCE;
            }
            if (wrapperClass == Long.class) {
                return LongCodec.INSTANCE;
            }
            if (wrapperClass == Double.class || wrapperClass == Float.class) {
                return DoubleCodec.INSTANCE;
            }
            return null;
        }
        return CODEC_MAP.computeIfAbsent(elementType, (t) -> new ObjectCodecableCodec(elementType, this.objectCodecService.codec(elementType)));
    }

    //    private List<Object> encodeKeys(List<String> keys) throws IOException {
    //        List<Object> encodeKeys = new ArrayList<>();
    //        for (String value : keys) {
    //            if (value != null) {
    //                encodeKeys.add(STRING_CODEC.getValueEncoder().encode(value));
    //            }
    //        }
    //        return encodeKeys;
    //    }

    private Object[] encodeArgs(List<?> values) throws IOException {
        Object[] arguments = new Object[values.size()];
        int index = 0;
        for (Object value : values) {
            if (value != null) {
                if (value instanceof CharSequence) {
                    arguments[index++] = value.toString();
                } else if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
                    arguments[index++] = String.valueOf(value);
                } else {
                    ObjectCodec<Object> codec = this.objectCodecService.codec(value.getClass());
                    arguments[index++] = codec.format(value);
                }
            }
        }
        return arguments;
    }

}
