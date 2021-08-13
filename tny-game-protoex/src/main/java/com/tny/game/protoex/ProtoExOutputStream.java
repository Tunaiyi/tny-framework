package com.tny.game.protoex;

import com.tny.game.common.buff.LinkedBuffer;
import com.tny.game.common.buff.*;
import com.tny.game.protoex.field.*;
import org.slf4j.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.*;

/**
 * ProtoEx类型输入流
 *
 * @author KGTny
 */
public class ProtoExOutputStream implements ProtoExStream, AutoCloseable {

	private static final Charset UTF8 = StandardCharsets.UTF_8;

	protected static final Logger LOGGER = LoggerFactory.getLogger(ProtoExOutputStream.class);

	private static final ProtoExSchemaContext NULL = null;

	private final ProtoExSchemaContext schemaContext;

	private ByteBufferAllocator allocator;

	private LinkedBuffer byteBuffer;

	public ProtoExOutputStream(ByteBufferAllocator allocator) {
		this.allocator = allocator;
		this.byteBuffer = new LinkedBuffer(this.allocator);
		this.schemaContext = DefaultProtoExSchemaContext.getDefault();
	}

	public ProtoExOutputStream() {
		this.allocator = new NioByteBufferAllocator();
		this.byteBuffer = new LinkedBuffer(this.allocator);
		this.schemaContext = DefaultProtoExSchemaContext.getDefault();
	}

	public ProtoExOutputStream(byte[] bytes) {
		this.byteBuffer = new LinkedBuffer(bytes, 0, 0);
		this.schemaContext = DefaultProtoExSchemaContext.getDefault();
	}

	public ProtoExOutputStream(byte[] bytes, int offset, int position) {
		this(null, bytes, offset, position);
	}

	private ProtoExOutputStream(ByteBufferAllocator allocator, byte[] bytes) {
		this(allocator, bytes, 0, 0);
	}

	private ProtoExOutputStream(ByteBufferAllocator allocator, byte[] bytes, int offset, int position) {
		if (allocator == null) {
			allocator = new NioByteBufferAllocator();
		}
		this.allocator = allocator;
		this.byteBuffer = new LinkedBuffer(bytes, offset, position);
		this.schemaContext = DefaultProtoExSchemaContext.getDefault();
	}

	public ProtoExOutputStream(int initSize) {
		this(NULL, initSize, LinkedBuffer.DEFAULT_BUFFER_SIZE);
	}

	public ProtoExOutputStream(int initSize, int growthSize) {
		this(NULL, initSize, growthSize);
	}

	public ProtoExOutputStream(ProtoExSchemaContext schemaContext) {
		this(schemaContext, LinkedBufferNode.DEFAULT_INIT_BUFFER_SIZE, LinkedBuffer.DEFAULT_BUFFER_SIZE);
	}

	public ProtoExOutputStream(ProtoExSchemaContext schemaContext, int initSize) {
		this(schemaContext, initSize, LinkedBuffer.DEFAULT_BUFFER_SIZE);
	}

	public ProtoExOutputStream(ProtoExSchemaContext schemaContext, int initSize, int nextSize) {
		if (schemaContext == null) {
			schemaContext = DefaultProtoExSchemaContext.getDefault();
		}
		this.schemaContext = schemaContext;
		this.byteBuffer = new LinkedBuffer(initSize, nextSize);
	}

	@Override
	public ProtoExSchemaContext getSchemaContext() {
		return this.schemaContext;
	}

	public LinkedBuffer getByteBuffer() {
		return this.byteBuffer;
	}

	public ProtoExOutputStream clear() {
		this.byteBuffer.clear();
		return this;
	}

	public void writeTag(int protoExID, boolean explicit, boolean rawType, int fieldNumber, FieldFormat formatType) {
		this.doWriteVarInt32(WireFormat.makeTypeTag(protoExID, explicit, rawType));
		this.doWriteVarInt32(WireFormat.makeFieldTag(fieldNumber, formatType));
	}

	public void writeChar(char value) {
		this.doWriteVarInt32(value);
	}

	public void writeShort(short value) {
		this.doWriteVarInt32(value);
	}

	public void writeSignShort(short value) {
		this.doWriteSInt32(value);
	}

	public void writeByte(byte value) {
		this.byteBuffer.write(value);
	}

	public void writeDouble(final double value) {
		//		this.doWriteLittleEndian64(Double.doubleToRawLongBits(value));
		this.doWriteVarInt64(Double.doubleToRawLongBits(value));
	}

	public void writeFloat(final float value) {
		//		this.doWriteLittleEndian32(Float.floatToRawIntBits(value));
		this.doWriteVarInt32(Float.floatToRawIntBits(value));
	}

	public void writeLong(final long value) {
		this.doWriteVarInt64(value);
	}

	public void writeSignLong(final long value) {
		this.doWriteSInt64(value);
	}

	public void writeFixLong(final long value) {
		this.doWriteFixed(value);
	}

	public void writeInt(final int value) {
		if (value >= 0) {
			this.doWriteVarInt32(value);
		} else {
			this.doWriteVarInt64(value);
		}
	}

	public void writeSignInt(final int value) {
		this.doWriteSInt32(value);
	}

	public void writeFixInt(final int value) {
		this.doWriteFixed32(value);
	}

	public void writeBoolean(final boolean value) {
		this.doWriteVarInt32(value ? 1 : 0);
	}

	public void writeEnum(final Enum<?> value) {
		EnumIO.writeNotTagTo(this, value);
	}

	public void writeBytes(byte[] value) {
		this.doWriteBytes(value);
	}

	public void writeBytes(byte[] value, int offset, int length) {
		this.doWriteBytes(value, offset, length);
	}

	/**
	 * Write a {@code string} field to the stream.
	 */
	public void writeString(final String value) {
		this.doWriteString(value);
	}

	public <T> void writeLengthLimitation(T value, FieldOptions<?> options, ProtoExSchema<T> schema) {
		this.doWriteLengthLimitation(value, options, schema);
	}

	private <T> void doWriteLengthLimitation(T value, FieldOptions<?> options, ProtoExSchema<T> schema) {
		if (value == null) {
			return;
		}
		if (schema.isRaw()
				&& schema.getProtoExId() != WireFormat.PROTO_ID_REPEAT
				&& schema.getProtoExId() != WireFormat.PROTO_ID_MAP) {
			throw ProtobufExException.rawTypeIsNoLengthLimitation(options.getDefaultType());
		}
		LinkedBuffer currentBuffer = this.byteBuffer;
		LinkedBuffer messageBuffer = this.byteBuffer.slice();
		// 先用 messageBuffer 替换调 当前 byteBuffer, 序列化 schema 的 value 后, 再切换回来.
		this.byteBuffer = messageBuffer;
		schema.writeValue(this, value, options);
		int messageSize = messageBuffer.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("protoExOutput.doWriteLengthLimitation 写入长度为 {} 的对象 : {}", messageSize, value);
		}
		LinkedBuffer sizeBuffer = messageBuffer.slice();
		this.byteBuffer = sizeBuffer;
		this.doWriteVarInt32(messageSize);
		LinkedBuffer remainBuffer = sizeBuffer.slice();
		this.byteBuffer = currentBuffer;
		this.byteBuffer.append(sizeBuffer);
		this.byteBuffer.append(messageBuffer);
		this.byteBuffer.append(remainBuffer);
	}

	private void doWriteString(final String value) {
		if (value == null) {
			return;
		}
		byte[] bytes = value.getBytes(UTF8);
		this.doWriteVarInt32(bytes.length);
		this.byteBuffer.write(bytes);
	}

	/**
	 * Write a {@code string} field to the stream.
	 */
	private void doWriteBytes(final byte[] value) {
		this.doWriteVarInt32(value.length);
		this.byteBuffer.write(value);
	}

	/**
	 * Write a {@code string} field to the stream.
	 */
	private void doWriteBytes(final byte[] value, int Offset, int length) {
		this.doWriteVarInt32(length);
		this.byteBuffer.write(value, Offset, length);
	}

	public void writeBytes(LinkedBuffer value) {
		this.doWriteVarInt32(value.size());
		this.byteBuffer.append(value);
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
		this.doWriteVarInt32(encodeZigZag32(value));
	}

	/**
	 * Write an {@code sint64} field to the stream.
	 */
	private void doWriteSInt64(final long value) {
		this.doWriteVarInt64(encodeZigZag64(value));
	}

	private void doWriteVarInt32(int value) {
		final int size = computeRawVarint32Size(value);
		if (size == 1) {
			this.byteBuffer.write((byte)value);
		} else {
			for (int i = 0, last = size - 1; i < last; i++, value >>>= 7) {
				this.byteBuffer.write((byte)((value & 0x7F) | 0x80));
			}
			this.byteBuffer.write((byte)value);
		}
	}

	public byte[] toByteArray() {
		return this.byteBuffer.toByteArray();
	}

	public void toBuffer(ByteBuffer buffer) {
		this.byteBuffer.toBuffer(buffer);
	}

	public void toBuffer(OutputStream output) throws IOException {
		this.byteBuffer.toBuffer(output);
	}

	private void doWriteLittleEndian64(long value) {
		this.byteBuffer.write((byte)(value & 0xFF))
				.write((byte)(value >> 8 & 0xFF))
				.write((byte)(value >> 16 & 0xFF))
				.write((byte)(value >> 24 & 0xFF))
				.write((byte)(value >> 32 & 0xFF))
				.write((byte)(value >> 40 & 0xFF))
				.write((byte)(value >> 48 & 0xFF))
				.write((byte)(value >> 56 & 0xFF));
	}

	private void doWriteLittleEndian32(int value) {
		this.byteBuffer.write((byte)(value & 0xFF))
				.write((byte)(value >> 8 & 0xFF))
				.write((byte)(value >> 16 & 0xFF))
				.write((byte)(value >> 24 & 0xFF));
	}

	/**
	 * Returns the buffer encoded with the variable int 32.
	 */
	private void doWriteVarInt64(long value) {
		final int size = computeRawVarInt64Size(value);
		if (size == 1) {
			this.byteBuffer.write((byte)value);
		} else {
			for (int i = 0, last = size - 1; i < last; i++, value >>>= 7)
				this.byteBuffer.write((byte)((value & 0x7F) | 0x80));
			this.byteBuffer.write((byte)value);
		}
	}

	private final static int MAX_INT_7_BIT = 0xffffffff << 7;

	private final static int MAX_INT_14_BIT = 0xffffffff << 14;

	private final static int MAX_INT_21_BIT = 0xffffffff << 21;

	private final static int MAX_INT_28_BIT = 0xffffffff << 28;

	/**
	 * Compute the number of bytes that would be needed to encode a varint. {@code value} is treated as unsigned, so it won't be sign-extended if
	 * negative.
	 */
	public static int computeRawVarint32Size(final int value) {
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

	private final static long MAX_LONG_7_BIT = 0xffffffffffffffffL << 7;

	private final static long MAX_LONG_14_BIT = 0xffffffffffffffffL << 14;

	private final static long MAX_LONG_21_BIT = 0xffffffffffffffffL << 21;

	private final static long MAX_LONG_28_BIT = 0xffffffffffffffffL << 28;

	private final static long MAX_LONG_35_BIT = 0xffffffffffffffffL << 35;

	private final static long MAX_LONG_42_BIT = 0xffffffffffffffffL << 42;

	private final static long MAX_LONG_49_BIT = 0xffffffffffffffffL << 49;

	private final static long MAX_LONG_56_BIT = 0xffffffffffffffffL << 56;

	private final static long MAX_LONG_63_BIT = 0xffffffffffffffffL << 63;

	/**
	 * Compute the number of bytes that would be needed to encode a varint.
	 */
	public static int computeRawVarInt64Size(final long value) {
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

	@Override
	public void close() {
		if (this.byteBuffer != null) {
			this.byteBuffer.release();
			this.byteBuffer = null;
		}
	}

}
