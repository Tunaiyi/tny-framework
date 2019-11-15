package com.tny.game.protoex;

import com.tny.game.protoex.annotations.*;

import java.lang.reflect.Type;
import java.util.*;

class PETKey {
    public static final boolean VAR_LENGTH = true;
    public static final boolean FIX_LENGTH = false;
}

public enum ProtoExType {

    /**
     * 自定义
     */
    MESSAGE(-1, PETKey.VAR_LENGTH) {
        @Override
        public boolean isExplicit(TypeEncode encode) {
            return encode.isExplicit(false);
        }

    },

    /**
     * char
     */
    CHAR(WireFormat.PROTO_ID_CHAR, PETKey.FIX_LENGTH),

    /**
     * short
     */
    SHORT(WireFormat.PROTO_ID_SHORT, PETKey.FIX_LENGTH),

    /**
     * byte
     */
    BYTE(WireFormat.PROTO_ID_BYTE, PETKey.FIX_LENGTH),

    /**
     * int
     */
    INT(WireFormat.PROTO_ID_INT, PETKey.FIX_LENGTH),

    /**
     * long
     */
    LONG(WireFormat.PROTO_ID_LONG, PETKey.FIX_LENGTH),

    /**
     * float
     */
    FLOAT(WireFormat.PROTO_ID_FLOAT, PETKey.FIX_LENGTH),

    /**
     * double
     */
    DOUBLE(WireFormat.PROTO_ID_DOUBLE, PETKey.FIX_LENGTH),

    /**
     * boolean
     */
    BOOLEAN(WireFormat.PROTO_ID_BOOLEAN, PETKey.FIX_LENGTH),

    /**
     * string
     */
    STRING(WireFormat.PROTO_ID_STRING, PETKey.VAR_LENGTH),

    /**
     * byte []
     */
    BYTES(WireFormat.PROTO_ID_BYTES, PETKey.VAR_LENGTH),

    /**
     * map
     */
    MAP(WireFormat.PROTO_ID_MAP, PETKey.VAR_LENGTH),

    /**
     * 重复类型 (Collection)
     */
    REPEAT(WireFormat.PROTO_ID_REPEAT, PETKey.VAR_LENGTH),

    /**
     * 枚举
     */
    ENUM(WireFormat.PROTO_ID_ENUM, PETKey.FIX_LENGTH) {
        @Override
        public boolean isExplicit(TypeEncode encode) {
            return encode.isExplicit(false);
        }

    };

    private static final Map<Type, ProtoExType> PROTO_EX_TYPE_MAP = new HashMap<Type, ProtoExType>();

    static {
        PROTO_EX_TYPE_MAP.put(Integer.TYPE, INT);
        PROTO_EX_TYPE_MAP.put(Integer.class, INT);
        PROTO_EX_TYPE_MAP.put(Long.TYPE, LONG);
        PROTO_EX_TYPE_MAP.put(Long.class, LONG);
        PROTO_EX_TYPE_MAP.put(Float.TYPE, FLOAT);
        PROTO_EX_TYPE_MAP.put(Float.class, FLOAT);
        PROTO_EX_TYPE_MAP.put(Double.TYPE, DOUBLE);
        PROTO_EX_TYPE_MAP.put(Double.class, DOUBLE);
        PROTO_EX_TYPE_MAP.put(Boolean.TYPE, BOOLEAN);
        PROTO_EX_TYPE_MAP.put(Boolean.class, BOOLEAN);
        PROTO_EX_TYPE_MAP.put(Character.TYPE, CHAR);
        PROTO_EX_TYPE_MAP.put(Character.class, CHAR);
        PROTO_EX_TYPE_MAP.put(Short.TYPE, SHORT);
        PROTO_EX_TYPE_MAP.put(Short.class, SHORT);
        PROTO_EX_TYPE_MAP.put(Byte.TYPE, BYTE);
        PROTO_EX_TYPE_MAP.put(Byte.class, BYTE);
        PROTO_EX_TYPE_MAP.put(String.class, STRING);
        PROTO_EX_TYPE_MAP.put(byte[].class, BYTES);

    }

    private final int id;

    private final boolean hasLength;

    ProtoExType(int id, boolean hasLength) {
        this.id = (byte) id;
        this.hasLength = hasLength;
    }

    public boolean isRaw() {
        return this.id > 0;
    }

    public int getId() {
        return this.id;
    }

    public boolean isHasLength() {
        return this.hasLength;
    }

    public boolean isExplicit(TypeEncode encode) {
        return true;
    }

    public static ProtoExType getProtoExType(Class<?> clazz) {
        if (Collection.class.isAssignableFrom(clazz)) {
            return REPEAT;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return MAP;
        } else if (clazz.isEnum()) {
            return ENUM;
        }
        ProtoExType type = PROTO_EX_TYPE_MAP.get(clazz);
        if (type != null)
            return type;
        return MESSAGE;
    }

    public static ProtoExType getRawType(int typeID) {
        if (typeID <= 0)
            return null;
        for (ProtoExType type : values()) {
            if (type.getId() == typeID)
                return type;
        }
        return null;
    }

}
