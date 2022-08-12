/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.message;

public class CodecConstants {

    public static final byte[] FRAME_MAGIC = "tny.".getBytes();

    public static final int OPTION_SIZE = 1;

    public static final int MESSAGE_LENGTH_SIZE = 4;

    public static final int PAYLOAD_LENGTH_BYTES_SIZE = 4;

    public static final int PAYLOAD_BYTES_MAX_SIZE = 0xFFFF; // 负载长读字节数

    public static final byte DATA_PACK_OPTION_MESSAGE_TYPE_SIZE = 2; // 包类型位数长度

    public static final byte DATA_PACK_OPTION_MESSAGE_TYPE_MASK = (byte)~(-1 << DATA_PACK_OPTION_MESSAGE_TYPE_SIZE); // 包类型掩码

    public static final byte DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_MESSAGE = (byte)0;

    public static final byte DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PING = (byte)1;

    public static final byte DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PONG = (byte)(1 << 1);

    // public static final byte DATA_PACK_OPTION_COMPRESS = (byte) (1 << 2); // 选项(是否有压缩)
    public static final byte DATA_PACK_OPTION_VERIFY = (byte)(1 << 2); // 选项(是否有校验)

    public static final byte DATA_PACK_OPTION_ENCRYPT = (byte)(1 << 3); // 选项(是否有加密)

    public static final byte DATA_PACK_OPTION_WASTE_BYTES = (byte)(1 << 4); // 选项(是否有加密)

    public static final byte MESSAGE_HEAD_OPTION_MODE_BIT_SIZE = 2;

    public static final byte MESSAGE_HEAD_OPTION_MODE_MASK = (byte)~(-1 << MESSAGE_HEAD_OPTION_MODE_BIT_SIZE); // 获取消息模式掩码 00000011

    public static final byte MESSAGE_HEAD_OPTION_MODE_VALUE_PING = (byte)-1;

    public static final byte MESSAGE_HEAD_OPTION_MODE_VALUE_PONG = (byte)-2;

    public static final byte MESSAGE_HEAD_OPTION_MODE_VALUE_REQUEST = (byte)0;

    public static final byte MESSAGE_HEAD_OPTION_MODE_VALUE_RESPONSE = (byte)1;

    public static final byte MESSAGE_HEAD_OPTION_MODE_VALUE_PUSH = 2;

    public static final byte MESSAGE_HEAD_OPTION_EXIST_BODY_BIT_SIZE = 1;

    public static final byte MESSAGE_HEAD_OPTION_EXIST_BODY_SHIFT = MESSAGE_HEAD_OPTION_MODE_BIT_SIZE; // line 掩码 00011100

    public static final byte MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST = (byte)(1 << MESSAGE_HEAD_OPTION_EXIST_BODY_SHIFT); // 是否请求数据

    public static final byte MESSAGE_HEAD_OPTION_EXIST_HEADERS_VALUE_EXIST = (byte)(1 << 6); // 是否请求数据

    public static final byte MESSAGE_HEAD_OPTION_LINE_MASK = (byte)(Byte.MAX_VALUE >>> 4 << 2); // line 掩码 00011100

    public static final byte MESSAGE_HEAD_OPTION_LINE_BIT_SIZE = 3;

    public static final byte MESSAGE_HEAD_OPTION_LINE_SHIFT = MESSAGE_HEAD_OPTION_MODE_BIT_SIZE + MESSAGE_HEAD_OPTION_EXIST_BODY_BIT_SIZE;

    public static final byte MESSAGE_HEAD_OPTION_LINE_VALUE_MIN = (byte)0; // line 小 0

    public static final byte MESSAGE_HEAD_OPTION_LINE_VALUE_MAX = (byte)(~(-1 << MESSAGE_HEAD_OPTION_LINE_BIT_SIZE)); // line 最大 00000111

    public static final int OPTION_LENGTH = 1;                  // 选项长度

    public static final int VERIFIER_CODE_LENGTH = 8;           // 校验码长度

    public static final int PACK_SIZE_LENGTH = 4;               // 校验码长度

    public static final int RANDOM_BYTES_SIZE = 32;     // 校验码长度

    public static final int RANDOM_WASTE_BIT_SIZE = 32; // 包体随机位移字节数

    /**
     * 是否是文件头
     *
     * @param magics
     * @return
     */
    public static boolean isMagic(final byte[] magics) {
        for (int index = 0; index < magics.length; index++) {
            if (CodecConstants.FRAME_MAGIC[index] != magics[index]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOption(byte option, byte mark) {
        return (option & mark) == mark;
    }

    public static boolean isOption(byte option, byte mark, byte value) {
        return (option & mark) == value;
    }

    public static byte setOption(byte option, byte value, boolean effect) {
        if (effect) {
            return (byte)(option | value);
        }
        return option;
    }

    public static void main(String[] args) {
        System.out.println(DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PONG);
        System.out.println((DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PONG & DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PONG) ==
                DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PONG);
        //        System.out.println(BytesAide.toBinaryString((byte)~(-1 << 3)));
        //        System.out.println(BytesAide.toBinaryString((byte)(Byte.MAX_VALUE >>> 4 << 2)));
    }

}
