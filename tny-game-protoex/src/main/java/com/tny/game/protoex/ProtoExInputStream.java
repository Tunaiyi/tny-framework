package com.tny.game.protoex;

import com.tny.game.protoex.field.IOConfiger;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * ProtoEx类型输入
 *
 * @author KGTny
 */
public class ProtoExInputStream implements ProtoExStream {

    protected static final Charset UTF8 = Charset.forName("UTF-8");

    private ProtoExSchemaContext schemaContext;
    private final ByteBuffer buffer;

    @Override
    public ProtoExSchemaContext getSchemaContext() {
        return this.schemaContext;
    }

    public ProtoExInputStream(byte[] buffer) {
        this(null, buffer);
    }

    public ProtoExInputStream(ProtoExSchemaContext schemaContext, byte[] buffer) {
        this.buffer = ByteBuffer.wrap(buffer);
        this.schemaContext = schemaContext;
        if (this.schemaContext == null)
            this.schemaContext = DefaultProtoExSchemaContext.getDefault();
    }

    public long remaining() {
        return buffer.remaining();
    }

    public Tag readTag() {
        int typeTag = this.doReadRawVarint32();
        int fieldTag = this.doReadRawVarint32();
        Tag tag = new Tag(typeTag, fieldTag);
        return tag;
    }

    public Tag getTag() {
        int position = this.buffer.position();
        Tag tag = this.readTag();
        this.buffer.position(position);
        return tag;
    }

    public char readChar() {
        return (char) this.doReadRawVarint32();
    }

    public short readShort() {
        return (short) this.doReadRawVarint32();
    }

    public short readSignShort() {
        return (short) this.doReadSInt32();
    }

    public byte readByte() {
        return (byte) this.doReadRawVarint32();
    }

    public double readDouble() {
        //		return Double.longBitsToDouble(this.doReadRawLittleEndian64());
        return Double.longBitsToDouble(this.doReadRawVarint64());
    }

    public float readFloat() {
        //		return Float.intBitsToFloat(this.doReadRawLittleEndian32());
        return Float.intBitsToFloat(this.doReadRawVarint32());
    }

    public long readLong() {
        return this.doReadRawVarint64();
    }

    public long readSignLong() {
        return this.doReadSInt64();
    }

    public long readFixLong() {
        return this.doReadRawLittleEndian64();
    }

    public int readInt() {
        return this.doReadRawVarint32();
    }

    public int readSignInt() {
        return this.doReadSInt32();
    }

    public int readFixedInt() {
        return this.doReadRawLittleEndian32();
    }

    public boolean readBoolean() {
        return this.buffer.get() > 0;
    }

    public <E extends Enum<E>> E readEnum(Class<E> clazz) {
        return EnumIO.readFrom(this, clazz);
    }

    public byte[] readBytes() {
        return this.doReadBytes();
    }

    public String readString() {
        return this.doReadString();
    }

    public <T> T readLengthLimitation(Tag tag, ProtoExSchema<T> schema, IOConfiger<?> conf) {
        return this.doReadLengthLimitation(tag, schema, conf);
    }

    private <T> T doReadLengthLimitation(Tag tag, ProtoExSchema<T> schema, IOConfiger<?> conf) {
        if (schema.isRaw())
            throw ProtobufExException.rawTypeIsNoLengthLimitation(conf.getDefaultType());
        final int length = this.doReadRawVarint32();
        if (length < 0)
            throw ProtobufExException.negativeSize(length);
        if (this.buffer.remaining() < length)
            throw ProtobufExException.notEnoughSize(length, this.buffer.remaining());
        int startAt = this.buffer.position();
        T value = schema.readValue(this, tag, conf);
        int endAt = this.buffer.position();
        int readSize = endAt - startAt;
        if (readSize != length)
            throw ProtobufExException.readErrorSizeBuffer(length, readSize);
        return value;
    }

    public void skipField(Tag tag) {
        if (tag.isRaw() && !ProtoExType.getRawType(tag.getProtoExID()).isHasLength()) {
            int protoExID = tag.getProtoExID();
            ProtoExSchema<?> schema = this.schemaContext.getSchema(protoExID, true);
            schema.readValue(this, tag, null);
        } else {
            final int size = this.doReadRawVarint32();
            if (size < 0)
                throw ProtobufExException.negativeSize(size);
            if (this.buffer.remaining() < size)
                throw ProtobufExException.notEnoughSize(size, this.buffer.remaining());
            this.buffer.position(this.buffer.position() + size);
        }
    }

    private int doReadSInt32() {
        return decodeZigZag32(this.doReadRawVarint32());
    }

    private long doReadSInt64() {
        return decodeZigZag32(this.doReadRawVarint64());
    }

    private String doReadString() {
        final int length = this.doReadRawVarint32();
        if (length < 0)
            throw ProtobufExException.negativeSize(length);

        if (this.buffer.remaining() < length)
            throw ProtobufExException.notEnoughSize(length, this.buffer.remaining());
        int position = this.buffer.position();
        try {
            String value = new String(this.buffer.array(), position, length, UTF8);
            return value;
        } finally {
            this.buffer.position(position + length);
        }
    }

    public static String cpString(ByteBuffer buffer) {
        final byte[] copy = new byte[buffer.array().length];
        buffer.get(copy);
        String value = new String(copy);
        buffer.position(0);
        return value;
    }

    public static String newString(ByteBuffer buffer) {
        byte[] data = buffer.array();
        return new String(data, 0, data.length);
    }

    private int doReadRawVarint32() {
        byte tmp = this.buffer.get();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7f;
        if ((tmp = this.buffer.get()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = this.buffer.get()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = this.buffer.get()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    result |= (tmp = this.buffer.get()) << 28;
                    if (tmp < 0) {
                        // Discard upper 32 bits.
                        for (int i = 0; i < 5; i++) {
                            if (this.buffer.get() >= 0) {
                                return result;
                            }
                        }
                        throw ProtobufExException.errorVarint();
                    }
                }
            }
        }
        return result;
    }

    private long doReadRawVarint64() {
        int shift = 0;
        long result = 0;
        while (shift < 64) {
            final byte b = this.buffer.get();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw ProtobufExException.errorVarint();
    }

    private int doReadRawLittleEndian32() {
        return ((this.buffer.get() & 0xff)) | ((this.buffer.get() & 0xff) << 8) | ((this.buffer.get() & 0xff) << 16) | ((this.buffer.get() & 0xff) << 24);
    }

    private long doReadRawLittleEndian64() {
        return (((long) this.buffer.get() & 0xff)) | (((long) this.buffer.get() & 0xff) << 8)
                | (((long) this.buffer.get() & 0xff) << 16) | (((long) this.buffer.get() & 0xff) << 24)
                | (((long) this.buffer.get() & 0xff) << 32) | (((long) this.buffer.get() & 0xff) << 40)
                | (((long) this.buffer.get() & 0xff) << 48) | (((long) this.buffer.get() & 0xff) << 56);
    }

    private byte[] doReadBytes() {
        final int length = this.doReadRawVarint32();
        if (length < 0)
            throw ProtobufExException.negativeSize(length);

        if (this.buffer.remaining() < length)
            throw ProtobufExException.notEnoughSize(length, this.buffer.remaining());

        final byte[] copy = new byte[length];
        this.buffer.get(copy);
        return copy;
    }

    //	public <T> T readMessage(int protoExID, boolean raw, boolean packed, FieldFormat format) {
    //		return doReadMessage(protoExID, raw, packed, format);
    //	}
    //
    //	public <T, C extends Collection<T>> Collection<T> readCollection(boolean packed, FieldFormat format, Class<?> defaultClass) {
    //		return doReadCollection(packed, format, defaultClass);
    //	}
    //
    //	private <T, C extends Collection<T>> Collection<T> doReadCollection(boolean packed, FieldFormat format, Class<?> defaultClass) {
    //		if (packed)
    //			return this.doReadPackeds(format);
    //		else
    //			return this.doReadUnpackeds(defaultClass);
    //	}
    //
    //	private <T> T doReadMessage(int protoExID, boolean raw, boolean packed, FieldFormat format) {
    //		ProtoExSchema<T> schema = this.schemaContext.getProtoSchema(protoExID, raw);
    //		return schema.readValue(this, packed, format);
    //	}
    //
    //	private <T, C extends Collection<T>> Collection<T> doReadPackeds(FieldFormat format) {
    //		//必定为 raw
    //		int protoExID = doReadRawVarint32();
    //		ProtoExSchema<T> schema = schemaContext.getProtoSchema(protoExID, true);
    //		int length = doReadRawVarint32();
    //		if (length <= 0)
    //			return Collections.emptyList();
    //		List<T> valueList = new ArrayList<T>();
    //		int startAt = buffer.position();
    //		int position = buffer.position();
    //		while (position - startAt < length) {
    //			T value = schema.readValue(this, true, format);
    //			if (value != null)
    //				valueList.add(value);
    //			position = buffer.position();
    //		}
    //		return valueList;
    //	}
    //
    //	private <T, C extends Collection<T>> Collection<T> doReadUnpackeds(Class<?> defaultClass) {
    //		Tag targetTag = getTag();
    //		Tag current = targetTag;
    //		List<T> valueList = new ArrayList<T>();
    //		if (targetTag.getFieldNumber() != current.getFieldNumber()) {
    //			ProtoExSchema<T> schema = schemaContext.getProtoSchema(current.getProtoExID(), current.isRaw(), defaultClass);
    //			T value = schema.readMessage(this);
    //			if (value != null)
    //				valueList.add(value);
    //			current = getTag();
    //		}
    //		return valueList;
    //	}

    private static int decodeZigZag32(final int n) {
        return (n >>> 1) ^ -(n & 1);
    }

    private static long decodeZigZag32(final long n) {
        return (n >>> 1) ^ -(n & 1);
    }

    public int position() {
        return this.buffer.position();
    }

    public int limit() {
        return this.buffer.limit();
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }

}
