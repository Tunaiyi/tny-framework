//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// http://code.google.com/p/protobuf/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
