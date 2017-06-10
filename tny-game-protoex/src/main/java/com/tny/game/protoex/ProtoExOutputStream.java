package com.tny.game.protoex;

import com.tny.game.common.utils.buff.ByteBufferNode;
import com.tny.game.common.utils.buff.LinkedByteBuffer;
import com.tny.game.protoex.field.FieldFormat;
import com.tny.game.protoex.field.IOConfiger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * ProtoEx类型输入流
 *
 * @author KGTny
 */
public class ProtoExOutputStream implements ProtoExStream {

    protected static final Charset UTF8 = Charset.forName("UTF-8");

    protected static final Logger LOGGER = LoggerFactory.getLogger(ProtoExOutputStream.class);
    private ProtoExSchemaContext schemaContext;

    private LinkedByteBuffer byteBuffer;

    public ProtoExOutputStream() {
        this.byteBuffer = new LinkedByteBuffer();
        this.schemaContext = DefaultProtoExSchemaContext.getDefault();
    }

    public ProtoExOutputStream(int initSize) {
        this(null, initSize, LinkedByteBuffer.DEFAULT_BUFFER_SIZE);
    }

    public ProtoExOutputStream(int initSize, int nextSize) {
        this(null, initSize, nextSize);
    }

    public ProtoExOutputStream(ProtoExSchemaContext schemaContext) {
        this(schemaContext, ByteBufferNode.DEFAULT_INIT_BUFFER_SIZE, LinkedByteBuffer.DEFAULT_BUFFER_SIZE);
    }

    public ProtoExOutputStream(ProtoExSchemaContext schemaContext, int initSize) {
        this(schemaContext, initSize, LinkedByteBuffer.DEFAULT_BUFFER_SIZE);
    }

    public ProtoExOutputStream(ProtoExSchemaContext schemaContext, int initSize, int nextSize) {
        if (schemaContext == null)
            schemaContext = DefaultProtoExSchemaContext.getDefault();
        this.schemaContext = schemaContext;
        this.byteBuffer = new LinkedByteBuffer(initSize, nextSize);
    }

    @Override
    public ProtoExSchemaContext getSchemaContext() {
        return this.schemaContext;
    }

    public LinkedByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    public ProtoExOutputStream clear() {
        this.byteBuffer.clear();
        return this;
    }

    public void writeTag(int protoExID, boolean explicit, boolean rawType, int fieldNumber, FieldFormat formatType) {
        this.doWriteVarint32(WireFormat.makeTypeTag(protoExID, explicit, rawType));
        this.doWriteVarint32(WireFormat.makeFieldTag(fieldNumber, formatType));
    }

    public void writeChar(char value) {
        this.doWriteVarint32(value);
    }

    public void writeShort(short value) {
        this.doWriteVarint32(value);
    }

    public void writeSignShort(short value) {
        this.doWriteSInt32(value);
    }

    public void writeByte(byte value) {
        this.doWriteVarint32(value);
    }

    public void writeDouble(final double value) {
        //		this.doWriteLittleEndian64(Double.doubleToRawLongBits(value));
        this.doWriteVarint64(Double.doubleToRawLongBits(value));
    }

    public void writeFloat(final float value) {
        //		this.doWriteLittleEndian32(Float.floatToRawIntBits(value));
        this.doWriteVarint32(Float.floatToRawIntBits(value));
    }

    public void writeLong(final long value) {
        this.doWriteVarint64(value);
    }

    public void writeSignLong(final long value) {
        this.doWriteSInt64(value);
    }

    public void writeFixLong(final long value) {
        this.doWriteFixed(value);
    }

    public void writeInt(final int value) {
        if (value >= 0) {
            this.doWriteVarint32(value);
        } else {
            this.doWriteVarint64(value);
        }
    }

    public void writeSignInt(final int value) {
        this.doWriteSInt32(value);
    }

    public void writeFixInt(final int value) {
        this.doWriteFixed32(value);
    }

    public void writeBoolean(final boolean value) {
        this.doWriteVarint32(value ? 1 : 0);
    }

    public void writeEnum(final Enum<?> value) {
        EnumIO.writeNotTagTo(this, value);
    }

    public void writeBytes(byte[] value) {
        this.doWriteBytes(value);
    }


    /**
     * Write a {@code string} field to the stream.
     */
    public void writeString(final String value) {
        this.doWriteString(value);
    }

    public <T> void writeLengthLimitation(T value, IOConfiger<?> conf, ProtoExSchema<T> schema) {
        this.doWriteLengthLimitation(value, conf, schema);
    }

    private <T> void doWriteLengthLimitation(T value, IOConfiger<?> conf, ProtoExSchema<T> schema) {
        if (value == null)
            return;
        if (schema.isRaw()
                && schema.getProtoExID() != WireFormat.PROTO_ID_REPEAT
                && schema.getProtoExID() != WireFormat.PROTO_ID_MAP)
            throw ProtobufExException.rawTypeIsNoLengthLimitation(conf.getDefaultType());
        LinkedByteBuffer currentBuffer = this.byteBuffer;
        ByteBufferNode remainNode = ByteBufferNode.cutBuffer(this.byteBuffer.getTail());
        LinkedByteBuffer messageBuffer = null;
        if (remainNode == null)
            messageBuffer = new LinkedByteBuffer(this.byteBuffer.getInitBufferSize(), this.byteBuffer.getNextBufferSize());
        else
            messageBuffer = new LinkedByteBuffer(remainNode);
        this.byteBuffer = messageBuffer;
        schema.writeValue(this, value, conf);
        int messageSize = messageBuffer.size();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("protoExOutput.doWriteLengthLimitation 写入 {} 长度为 {} 的对象 : {}", messageSize, value);
        final int size = computeRawVarint32Size(messageSize);
        LinkedByteBuffer sizeBuffer = new LinkedByteBuffer(size, 1);
        this.byteBuffer = sizeBuffer;
        this.doWriteVarint32(messageSize);
        this.byteBuffer = currentBuffer;
        this.byteBuffer.appand(sizeBuffer);
        this.byteBuffer.appand(messageBuffer);
    }

    private void doWriteString(final String value) {
        if (value == null)
            return;
        byte[] bytes = value.getBytes(UTF8);
        this.doWriteVarint32(bytes.length);
        this.byteBuffer.write(bytes);
    }

    /**
     * Write a {@code string} field to the stream.
     */
    private void doWriteBytes(final byte[] value) {
        this.doWriteVarint32(value.length);
        this.byteBuffer.write(value);
    }


    public void writeBytes(LinkedByteBuffer value) {
        this.doWriteVarint32(value.size());
        this.byteBuffer.appand(value);
    }

    /**
     * Write a {@code fixed64} field to the stream.
     */
    private void doWriteFixed(final long value) {
        this.doWriteLittleEndian64(value);
    }

    /**
     * Write a {@code fixed32} field to the stream.
     */
    private void doWriteFixed32(final int value) {
        this.doWriteLittleEndian32(value);
    }

    /**
     * Write an {@code sint32} field to the stream.
     */
    private void doWriteSInt32(final int value) {
        this.doWriteVarint32(encodeZigZag32(value));
    }

    /**
     * Write an {@code sint64} field to the stream.
     */
    private void doWriteSInt64(final long value) {
        this.doWriteVarint64(encodeZigZag64(value));
    }

    private void doWriteVarint32(int value) {
        final int size = computeRawVarint32Size(value);
        if (size == 1) {
            this.byteBuffer.write((byte) value);
        } else {
            for (int i = 0, last = size - 1; i < last; i++, value >>>= 7) {
                this.byteBuffer.write((byte) ((value & 0x7F) | 0x80));
            }
            this.byteBuffer.write((byte) value);
        }
    }

    public byte[] toByteArray() {
        return this.byteBuffer.toByteArray();
    }

    private void doWriteLittleEndian64(long value) {
        this.byteBuffer.write((byte) (value & 0xFF))
                .write((byte) (value >> 8 & 0xFF))
                .write((byte) (value >> 16 & 0xFF))
                .write((byte) (value >> 24 & 0xFF))
                .write((byte) (value >> 32 & 0xFF))
                .write((byte) (value >> 40 & 0xFF))
                .write((byte) (value >> 48 & 0xFF))
                .write((byte) (value >> 56 & 0xFF));
    }

    private void doWriteLittleEndian32(int value) {
        this.byteBuffer.write((byte) (value & 0xFF))
                .write((byte) (value >> 8 & 0xFF))
                .write((byte) (value >> 16 & 0xFF))
                .write((byte) (value >> 24 & 0xFF));
    }

    /**
     * Returns the buffer encoded with the variable int 32.
     */
    private void doWriteVarint64(long value) {
        final int size = computeRawVarint64Size(value);
        if (size == 1)
            this.byteBuffer.write((byte) value);
        else {
            for (int i = 0, last = size - 1; i < last; i++, value >>>= 7)
                this.byteBuffer.write((byte) ((value & 0x7F) | 0x80));
            this.byteBuffer.write((byte) value);
        }
    }

    private static int MAX_INT_7_BIT = 0xffffffff << 7;
    private static int MAX_INT_14_BIT = 0xffffffff << 14;
    private static int MAX_INT_21_BIT = 0xffffffff << 21;
    private static int MAX_INT_28_BIT = 0xffffffff << 28;

    /**
     * Compute the number of bytes that would be needed to encode a varint. {@code value} is treated as unsigned, so it won't be sign-extended if
     * negative.
     */
    public static int computeRawVarint32Size(final int value) {
        if ((value & (MAX_INT_7_BIT)) == 0)
            return 1;
        if ((value & (MAX_INT_14_BIT)) == 0)
            return 2;
        if ((value & (MAX_INT_21_BIT)) == 0)
            return 3;
        if ((value & (MAX_INT_28_BIT)) == 0)
            return 4;
        return 5;
    }

    private static long MAX_LONG_7_BIT = 0xffffffffffffffffL << 7;
    private static long MAX_LONG_14_BIT = 0xffffffffffffffffL << 14;
    private static long MAX_LONG_21_BIT = 0xffffffffffffffffL << 21;
    private static long MAX_LONG_28_BIT = 0xffffffffffffffffL << 28;
    private static long MAX_LONG_35_BIT = 0xffffffffffffffffL << 35;
    private static long MAX_LONG_42_BIT = 0xffffffffffffffffL << 42;
    private static long MAX_LONG_49_BIT = 0xffffffffffffffffL << 49;
    private static long MAX_LONG_56_BIT = 0xffffffffffffffffL << 56;
    private static long MAX_LONG_63_BIT = 0xffffffffffffffffL << 63;

    /**
     * Compute the number of bytes that would be needed to encode a varint.
     */
    public static int computeRawVarint64Size(final long value) {
        if ((value & MAX_LONG_7_BIT) == 0)
            return 1;
        if ((value & MAX_LONG_14_BIT) == 0)
            return 2;
        if ((value & MAX_LONG_21_BIT) == 0)
            return 3;
        if ((value & MAX_LONG_28_BIT) == 0)
            return 4;
        if ((value & MAX_LONG_35_BIT) == 0)
            return 5;
        if ((value & MAX_LONG_42_BIT) == 0)
            return 6;
        if ((value & MAX_LONG_49_BIT) == 0)
            return 7;
        if ((value & MAX_LONG_56_BIT) == 0)
            return 8;
        if ((value & MAX_LONG_63_BIT) == 0)
            return 9;
        return 10;
    }

    public static int encodeZigZag32(final int n) {
        // Note:  the right-shift must be arithmetic
        return (n << 1) ^ (n >> 31);
    }

    public static long encodeZigZag64(final long n) {
        // Note:  the right-shift must be arithmetic
        return (n << 1) ^ (n >> 63);
    }

    public int size() {
        return this.byteBuffer.size();
    }

}
