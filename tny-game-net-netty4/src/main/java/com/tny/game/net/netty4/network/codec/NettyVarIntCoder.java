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

package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.exception.*;
import io.netty.buffer.ByteBuf;

/**
 * Created by Kun Yang on 2018/8/9.
 */
public class NettyVarIntCoder {

    private final static int MAX_INT_7_BIT = 0xffffffff << 7;

    private final static int MAX_INT_14_BIT = 0xffffffff << 14;

    private final static int MAX_INT_21_BIT = 0xffffffff << 21;

    private final static int MAX_INT_28_BIT = 0xffffffff << 28;

    private final static long MAX_LONG_7_BIT = 0xffffffffffffffffL << 7;

    private final static long MAX_LONG_14_BIT = 0xffffffffffffffffL << 14;

    private final static long MAX_LONG_21_BIT = 0xffffffffffffffffL << 21;

    private final static long MAX_LONG_28_BIT = 0xffffffffffffffffL << 28;

    private final static long MAX_LONG_35_BIT = 0xffffffffffffffffL << 35;

    private final static long MAX_LONG_42_BIT = 0xffffffffffffffffL << 42;

    private final static long MAX_LONG_49_BIT = 0xffffffffffffffffL << 49;

    private final static long MAX_LONG_56_BIT = 0xffffffffffffffffL << 56;

    private final static long MAX_LONG_63_BIT = 0xffffffffffffffffL << 63;

    private NettyVarIntCoder() {
    }

    /**
     * Compute the number of bytes that would be needed to encode a varint. {@code value} is treated as unsigned, so it won't be sign-extended if
     * negative.
     */
    public static int varInt2Size(final int value) {
        if ((value & (MAX_INT_7_BIT)) == 0) {
            return 1;
        }
        if ((value & (MAX_INT_14_BIT)) == 0) {
            return 2;
        }
        if ((value & (MAX_INT_21_BIT)) == 0) {
            return 3;
        }
        if ((value & (MAX_INT_28_BIT)) == 0) {
            return 4;
        }
        return 5;
    }

    /**
     * Compute the number of bytes that would be needed to encode a varint.
     */
    public static int varInt64Size(final long value) {
        if ((value & MAX_LONG_7_BIT) == 0) {
            return 1;
        }
        if ((value & MAX_LONG_14_BIT) == 0) {
            return 2;
        }
        if ((value & MAX_LONG_21_BIT) == 0) {
            return 3;
        }
        if ((value & MAX_LONG_28_BIT) == 0) {
            return 4;
        }
        if ((value & MAX_LONG_35_BIT) == 0) {
            return 5;
        }
        if ((value & MAX_LONG_42_BIT) == 0) {
            return 6;
        }
        if ((value & MAX_LONG_49_BIT) == 0) {
            return 7;
        }
        if ((value & MAX_LONG_56_BIT) == 0) {
            return 8;
        }
        if ((value & MAX_LONG_63_BIT) == 0) {
            return 9;
        }
        return 10;
    }

    public static void writeVarInt32(int value, ByteBuf buf) {
        final int size = varInt2Size(value);
        if (size == 1) {
            buf.writeByte((byte) value);
        } else {
            for (int i = 0, last = size - 1; i < last; i++, value >>>= 7) {
                buf.writeByte((byte) ((value & 0x7F) | 0x80));
            }
            buf.writeByte((byte) value);
        }
    }

    public static void writeVarInt64(long value, ByteBuf buf) {
        final int size = varInt64Size(value);
        if (size == 1) {
            buf.writeByte((byte) value);
        } else {
            for (int i = 0, last = size - 1; i < last; i++, value >>>= 7) {
                buf.writeByte((byte) ((value & 0x7F) | 0x80));
            }
            buf.writeByte((byte) value);
        }
    }

    public static int readVarInt32(ByteBuf buf) {
        byte tmp = buf.readByte();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7f;
        if ((tmp = buf.readByte()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = buf.readByte()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = buf.readByte()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    result |= (tmp = buf.readByte()) << 28;
                    if (tmp < 0) {
                        // Discard upper 32 bits.
                        for (int i = 0; i < 5; i++) {
                            if (buf.readByte() >= 0) {
                                return result;
                            }
                        }
                        throw NetCodecException.causeDecodeError("error varint");
                    }
                }
            }
        }
        return result;
    }

    public static long readVarInt64(ByteBuf buf) {
        int shift = 0;
        long result = 0;
        while (shift < 64) {
            final byte b = buf.readByte();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw NetCodecException.causeDecodeError("error varint");
    }

    public static int readFixed32(ByteBuf buffer) {
        return ((buffer.readByte() & 0xff))
               | ((buffer.readByte() & 0xff) << 8)
               | ((buffer.readByte() & 0xff) << 16)
               | ((buffer.readByte() & 0xff) << 24);
    }

    public static long readFixed64(ByteBuf buffer) {
        return (((long) buffer.readByte() & 0xff))
               | (((long) buffer.readByte() & 0xff) << 8)
               | (((long) buffer.readByte() & 0xff) << 16)
               | (((long) buffer.readByte() & 0xff) << 24)
               | (((long) buffer.readByte() & 0xff) << 32)
               | (((long) buffer.readByte() & 0xff) << 40)
               | (((long) buffer.readByte() & 0xff) << 48)
               | (((long) buffer.readByte() & 0xff) << 56);
    }

    public static void writeFixed32(int value, ByteBuf buffer) {
        buffer.writeByte((byte) (value & 0xFF))
                .writeByte((byte) (value >> 8 & 0xFF))
                .writeByte((byte) (value >> 16 & 0xFF))
                .writeByte((byte) (value >> 24 & 0xFF));
    }

    public static void writeFixed64(long value, ByteBuf buffer) {
        buffer.writeByte((byte) (value & 0xFF))
                .writeByte((byte) (value >> 8 & 0xFF))
                .writeByte((byte) (value >> 16 & 0xFF))
                .writeByte((byte) (value >> 24 & 0xFF))
                .writeByte((byte) (value >> 32 & 0xFF))
                .writeByte((byte) (value >> 40 & 0xFF))
                .writeByte((byte) (value >> 48 & 0xFF))
                .writeByte((byte) (value >> 56 & 0xFF));
    }

}
