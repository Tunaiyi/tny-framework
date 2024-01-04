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

package com.tny.game.net.message.codec;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 12:21 上午
 */
public interface ProtobufConstants {

    int PROTOBUF_HEAD_SIZE = 0;

    int PROTOBUF_HEAD_TYPE_BIT_SIZE = 5;
    byte PROTOBUF_HEAD_TYPE_BIT_MASK = 0xFF >> (8 - PROTOBUF_HEAD_TYPE_BIT_SIZE);

    int PROTOBUF_HEAD_MULTI_BIT_SIZE = 2;
    byte PROTOBUF_HEAD_SINGLE_VALUE = 0;
    //    byte PROTOBUF_HEAD_LIST_VALUE = 1 << PROTOBUF_HEAD_TYPE_BIT_SIZE;
    byte PROTOBUF_HEAD_ARRAY_VALUE = 2 << PROTOBUF_HEAD_TYPE_BIT_SIZE;

    byte PROTOBUF_RAW_TYPE_ID_NULL = (byte) 0;

    byte PROTOBUF_RAW_TYPE_ID_BYTE = (byte) 1;
    //    byte PROTOBUF_RAW_TYPE_ID_BYTE_LIST = (byte)1 | PROTOBUF_HEAD_LIST_VALUE;

    byte PROTOBUF_RAW_TYPE_ID_SHORT = (byte) 2;
    //    byte PROTOBUF_RAW_TYPE_ID_SHORT_LIST = (byte)2 | PROTOBUF_HEAD_LIST_VALUE;

    byte PROTOBUF_RAW_TYPE_ID_INT = (byte) 3;
    //    byte PROTOBUF_RAW_TYPE_ID_INT_LIST = (byte)3 | PROTOBUF_HEAD_LIST_VALUE;

    byte PROTOBUF_RAW_TYPE_ID_LONG = (byte) 4;
    //    byte PROTOBUF_RAW_TYPE_ID_LONG_LIST = (byte)4 | PROTOBUF_HEAD_LIST_VALUE;

    byte PROTOBUF_RAW_TYPE_ID_FLOAT = (byte) 5;
    //    byte PROTOBUF_RAW_TYPE_ID_FLOAT_LIST = (byte)5 | PROTOBUF_HEAD_LIST_VALUE;

    byte PROTOBUF_RAW_TYPE_ID_DOUBLE = (byte) 6;
    //    byte PROTOBUF_RAW_TYPE_ID_DOUBLE_LIST = (byte)6 | PROTOBUF_HEAD_LIST_VALUE;

    byte PROTOBUF_RAW_TYPE_ID_BOOL = (byte) 7;
    //    byte PROTOBUF_RAW_TYPE_ID_BOOL_LIST = (byte)7 | PROTOBUF_HEAD_LIST_VALUE;

    byte PROTOBUF_RAW_TYPE_ID_STRING = (byte) 8;
    //    byte PROTOBUF_RAW_TYPE_ID_STRING_LIST = (byte)8 | PROTOBUF_HEAD_LIST_VALUE;

    byte PROTOBUF_RAW_TYPE_ID_COMPLEX = (byte) 9;

    byte PROTOBUF_MESSAGE_PARAMS = (byte) 0x80;

}
