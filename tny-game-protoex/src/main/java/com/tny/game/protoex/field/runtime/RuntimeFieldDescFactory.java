package com.tny.game.protoex.field.runtime;

import com.tny.game.common.buff.*;
import com.tny.game.protoex.field.*;

import java.util.*;

/**
 * 运行时字段描述工厂
 *
 * @author KGTny
 */
public class RuntimeFieldDescFactory {

    private static final HashMap<Class<?>, FieldDescFactory<?>> factotyMap = new HashMap<Class<?>, FieldDescFactory<?>>();

    static final FieldDescFactory<Boolean> BOOL;
    static final FieldDescFactory<Byte> BYTE;
    static final FieldDescFactory<byte[]> BYTE_ARRAY;
    static final FieldDescFactory<Character> CHAR;
    static final FieldDescFactory<Double> DOUBLE;
    static final FieldDescFactory<Float> FLOAT;
    static final FieldDescFactory<Integer> INT32;
    static final FieldDescFactory<Long> INT64;
    static final FieldDescFactory<Short> SHORT;
    static final FieldDescFactory<String> STRING;

    static final FieldDescFactory<Enum<?>> ENUM;
    static final FieldDescFactory<Object> OBJECT;

    static final FieldDescFactory<Object> REPEAT;
    static final FieldDescFactory<Object> MAP;
    static final FieldDescFactory<LinkedByteBuffer> LINKED_BYTE_BUFFER;
    //	static final FieldDescFactory<Object> ARRAY;

    static {

        BOOL = RuntimeUnsafeFieldDescFactory.BOOLEAN;
        BYTE = RuntimeUnsafeFieldDescFactory.BYTE;
        BYTE_ARRAY = RuntimeUnsafeFieldDescFactory.BYTE_ARRAY;
        CHAR = RuntimeUnsafeFieldDescFactory.CHAR;
        DOUBLE = RuntimeUnsafeFieldDescFactory.DOUBLE;
        FLOAT = RuntimeUnsafeFieldDescFactory.FLOAT;
        INT32 = RuntimeUnsafeFieldDescFactory.INTEGER;
        INT64 = RuntimeUnsafeFieldDescFactory.LONG;
        SHORT = RuntimeUnsafeFieldDescFactory.SHORT;
        STRING = RuntimeUnsafeFieldDescFactory.STRING;
        LINKED_BYTE_BUFFER = RuntimeUnsafeFieldDescFactory.LINKED_BYTE_BUFFER;

        ENUM = RuntimeUnsafeFieldDescFactory.ENUM;
        OBJECT = RuntimeUnsafeFieldDescFactory.OBJECT;

        REPEAT = RuntimeUnsafeFieldDescFactory.REPEAT;
        MAP = RuntimeUnsafeFieldDescFactory.MAP;
        //		ARRAY = RuntimeUnsafeFieldDescFactory.ARRAY;

        factotyMap.put(Integer.TYPE, INT32);
        factotyMap.put(Integer.class, INT32);
        factotyMap.put(Long.TYPE, INT64);
        factotyMap.put(Long.class, INT64);
        factotyMap.put(Float.TYPE, FLOAT);
        factotyMap.put(Float.class, FLOAT);
        factotyMap.put(Double.TYPE, DOUBLE);
        factotyMap.put(Double.class, DOUBLE);
        factotyMap.put(Boolean.TYPE, BOOL);
        factotyMap.put(Boolean.class, BOOL);
        factotyMap.put(Character.TYPE, CHAR);
        factotyMap.put(Character.class, CHAR);
        factotyMap.put(Short.TYPE, SHORT);
        factotyMap.put(Short.class, SHORT);
        factotyMap.put(Byte.TYPE, BYTE);
        factotyMap.put(Byte.class, BYTE);
        factotyMap.put(String.class, STRING);
        factotyMap.put(byte[].class, BYTE_ARRAY);
        factotyMap.put(LinkedByteBuffer.class, LINKED_BYTE_BUFFER);

        factotyMap.put(int[].class, REPEAT);
        factotyMap.put(long[].class, REPEAT);
        factotyMap.put(float[].class, REPEAT);
        factotyMap.put(double[].class, REPEAT);
        factotyMap.put(boolean[].class, REPEAT);
        factotyMap.put(char[].class, REPEAT);
        factotyMap.put(short[].class, REPEAT);
        // factotyMap.put(byte[].class, REPEAT);

    }

    /**
     * 根据类型获取字段描述工厂对象
     *
     * @param type
     * @return
     */
    public static FieldDescFactory<?> getFactory(Class<?> type) {
        FieldDescFactory<?> factory = factotyMap.get(type);
        if (factory != null)
            return factory;
        else if (type.isEnum())
            return ENUM;
        else if (type.isArray() || Collection.class.isAssignableFrom(type))
            return REPEAT;
        else if (Map.class.isAssignableFrom(type))
            return MAP;
        return OBJECT;
    }

}
