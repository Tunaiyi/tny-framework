/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex;

import com.tny.game.protoex.field.*;

/**
 * This class is used internally by the Protocol Buffer library and generated
 * message implementations. It is public only because those generated messages
 * do not reside in the {@code protobuf} package. Others should not use this
 * class directly.
 * <p>
 * This class contains constants and helper functions useful for dealing with
 * the Protocol Buffer wire format.
 *
 * @author kenton@google.com Kenton Varda
 * @author David Yu
 */
public final class WireFormat {

    // Do not allow instantiation.
    private WireFormat() {
    }

    public final static int MAX_PROTO_EX_ID = 536870911;

    public final static int MIN_PROTO_EX_ID = 1;

    public final static int MAX_FIELD_NUMBER = 536870911;

    public final static int MIN_FIELD_NUMBER = 1;

    public final static int PROTO_ID_CHAR = 1;

    public final static int PROTO_ID_SHORT = 2;

    public final static int PROTO_ID_BYTE = 3;

    public final static int PROTO_ID_INT = 4;

    public final static int PROTO_ID_LONG = 5;

    public final static int PROTO_ID_FLOAT = 6;

    public final static int PROTO_ID_DOUBLE = 7;

    public final static int PROTO_ID_BOOLEAN = 8;

    public final static int PROTO_ID_STRING = 9;

    public final static int PROTO_ID_BYTES = 10;

    public final static int PROTO_ID_MAP = 11;

    public final static int PROTO_ID_REPEAT = 12;

    public final static int PROTO_ID_ENUM = 13;

    // public final static AtomicInteger PROTO_ID_ATOMIC_INT = 24;
    // public final static AtomicLong PROTO_ID_ATOMIC_LONG = 25;
    // public final static AtomicDouble PROTO_ID_ATOMIC_DOUBLE = 27;
    // public final static AtomicBoolean PROTO_ID_ATOMIC_BOOLEAN = 28;

    //	public static final int WIRETYPE_VARINT = 0;
    //	public static final int WIRETYPE_FIXED64 = 1;
    //	public static final int WIRETYPE_LENGTH_DELIMITED = 2;
    //	public static final int WIRETYPE_START_GROUP = 3;
    //	public static final int WIRETYPE_END_GROUP = 4;
    //	public static final int WIRETYPE_FIXED32 = 5;
    //	public static final int WIRETYPE_REFERENCE = 6;
    //	public static final int WIRETYPE_TAIL_DELIMITER = 7;

    private static final int TYPE_TAG_KIND_BITS = 1;

    private static final int TYPE_TAG_KIND_MASK = 1 << 1;

    private static final int TYPE_TAG_MARK_BITS = 1;

    private static final int TYPE_TAG_MARK_MASK = 1;

    private static final int REPEAT_OPTION_PACKED_BITS = 1;

    private static final int REPEAT_OPTION_RAW_BITS = 1;

    private static final int REPEAT_OPTION_PACKED_MASK = 1;

    private static final int REPEAT_OPTION_RAW_MASK = 1 << 1;

    private static final int FIELD_TAG_FORMAT_BITS = 2;

    private static final int FIELD_TAG_FORMAT_MASK = 1 << 1 | 1;

    public static boolean checkProtoExId(int protoExID) {
        return WireFormat.MIN_PROTO_EX_ID <= protoExID && protoExID <= WireFormat.MAX_PROTO_EX_ID;
    }

    public static boolean checkFieldNumber(int protoExID) {
        return WireFormat.MIN_FIELD_NUMBER <= protoExID && protoExID <= WireFormat.MAX_FIELD_NUMBER;
    }

    public static boolean checFieldNumber(int fieldNumber) {
        return fieldNumber > 0 && fieldNumber <= MAX_FIELD_NUMBER;
    }

    public static boolean isTypeTagExplicit(int tag) {
        return (tag & TYPE_TAG_KIND_MASK) > 0;
    }

    public static boolean isRawType(int tag) {
        return (tag & TYPE_TAG_MARK_MASK) == 0;
    }

    public static int typeTag2ProtoExId(int tag) {
        return tag >>> TYPE_TAG_KIND_BITS >>> TYPE_TAG_MARK_BITS;
    }

    public static FieldFormat fieldTag2FieldFormat(int tag) {
        int formatType = tag & FIELD_TAG_FORMAT_MASK;
        return FieldFormat.get(formatType);
    }

    public static int fieldTag2FieldNumber(int tag) {
        return tag >>> FIELD_TAG_FORMAT_BITS;
    }

    public static int makeTypeTag(int protoExID, boolean explicit, boolean rawType) {
        protoExID = explicit ? protoExID : 0;
        return (((protoExID << 2) | (explicit ? 2 : 0)) | (rawType ? 0 : 1));
    }

    public static int makeFieldTag(int fieldNumber, final FieldFormat format) {
        return (fieldNumber << FIELD_TAG_FORMAT_BITS) | format.ID;
    }

    public static int makeRepeatOption(int protoExID, boolean rawType, boolean packed) {
        return (((protoExID << 2) | (rawType ? 0 : 2))) | (packed ? 1 : 0);
    }

    public static boolean isRepeatPacked(int option) {
        return (option & REPEAT_OPTION_PACKED_MASK) > 0;
    }

    public static boolean isRepeatChildRaw(int option) {
        return (option & REPEAT_OPTION_RAW_MASK) == 0;
    }

    public static int getRepeatChildId(int option) {
        return option >>> (REPEAT_OPTION_RAW_BITS + REPEAT_OPTION_PACKED_BITS);
    }

    /**
     * This is here to support runtime schemas.
     */
    public enum JavaType {
        //		BYTE_STRING(ByteString.EMPTY),
        INT(0),
        LONG(0L),
        FLOAT(0F),
        DOUBLE(0D),
        BOOLEAN(false),
        STRING(""),
        ENUM(null),
        MESSAGE(null);

        JavaType(final Object defaultDefault) {
            this.defaultDefault = defaultDefault;
        }

        /**
         * The default default value for fields of this type, if it's a
         * primitive type.
         */
        Object getDefaultDefault() {
            return this.defaultDefault;
        }

        private final Object defaultDefault;
    }

    // Field numbers for feilds in MessageSet wire format.
    // static final int MESSAGE_SET_ITEM = 1;
    // static final int MESSAGE_SET_TYPE_ID = 2;
    // static final int MESSAGE_SET_MESSAGE = 3;

}
