package com.tny.game.net.netty4.codec;

import com.tny.game.net.exception.*;
import com.tny.game.protoex.*;
import io.netty.buffer.ByteBuf;

/**
 * Created by Kun Yang on 2018/8/9.
 */
public class NettyVarIntCoder {

    private NettyVarIntCoder() {
    }

    /**
     * Compute the number of bytes that would be needed to encode a varint. {@code value} is treated as unsigned, so it won't be sign-extended if
     * negative.
     */
    public static int varInt2Size(final int value) {
        return ProtoExOutputStream.computeRawVarint32Size(value);
    }

    /**
     * Compute the number of bytes that would be needed to encode a varint.
     */
    public static int varInt64Size(final long value) {
        return ProtoExOutputStream.computeRawVarInt64Size(value);
    }

    public static void writeVarint32(int value, ByteBuf buf) {
        final int size = varInt2Size(value);
        if (size == 1) {
            buf.writeByte((byte)value);
        } else {
            for (int i = 0, last = size - 1; i < last; i++, value >>>= 7) {
                buf.writeByte((byte)((value & 0x7F) | 0x80));
            }
            buf.writeByte((byte)value);
        }
    }

    public static void writeVarInt64(long value, ByteBuf buf) {
        final int size = varInt64Size(value);
        if (size == 1) {
            buf.writeByte((byte)value);
        } else {
            for (int i = 0, last = size - 1; i < last; i++, value >>>= 7) {
                buf.writeByte((byte)((value & 0x7F) | 0x80));
            }
            buf.writeByte((byte)value);
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
                        throw CodecException.causeDecode("error varint");
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
            result |= (long)(b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw CodecException.causeDecode("error varint");
    }

}
