package com.tny.game.protoex.field.runtime;

import com.tny.game.common.buff.LinkedBuffer;
import com.tny.game.protoex.*;
import com.tny.game.protoex.field.*;

import java.util.concurrent.atomic.*;

/**
 * 原生类型描述框架
 *
 * @param <T>
 * @author KGTny
 */
public abstract class RuntimePrimitiveSchema<T> extends BaseProtoExSchema<T> {

	protected RuntimePrimitiveSchema(int protoExID, Class<T> clazz) {
		super(protoExID, true, clazz.getName());
	}

	@Override
	public void writeMessage(ProtoExOutputStream outputStream, T value, IOConfiger<?> ioDesc) {
		this.writeTag(outputStream, ioDesc);
		this.writeValue(outputStream, value, ioDesc);
	}

	public final static ProtoExSchema<Character> CHAR_SCHEMA = new RuntimePrimitiveSchema<Character>(WireFormat.PROTO_ID_CHAR, Character.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, Character value, IOConfiger<?> conf) {
			conf.getFormat().writeNoTag(value.charValue(), outputStream);
		}

		@Override
		public Character readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return inputStream.readChar();
		}

	};

	public final static ProtoExSchema<Short> SHORT_SCHEMA = new RuntimePrimitiveSchema<Short>(WireFormat.PROTO_ID_SHORT, Short.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, Short value, IOConfiger<?> conf) {
			conf.getFormat().writeNoTag(value, outputStream);
		}

		@Override
		public Short readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return (short)tag.getFormat().readInt(inputStream);
		}

	};

	public final static ProtoExSchema<Byte> BYTE_SCHEMA = new RuntimePrimitiveSchema<Byte>(WireFormat.PROTO_ID_BYTE, Byte.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, Byte value, IOConfiger<?> conf) {
			outputStream.writeByte(value);
		}

		@Override
		public Byte readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return inputStream.readByte();
		}

	};

	public final static ProtoExSchema<Integer> INT_SCHEMA = new RuntimePrimitiveSchema<Integer>(WireFormat.PROTO_ID_INT, Integer.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, Integer value, IOConfiger<?> conf) {
			conf.getFormat().writeNoTag(value, outputStream);
		}

		@Override
		public Integer readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return tag.getFormat().readInt(inputStream);
		}

	};

	public final static ProtoExSchema<AtomicInteger> ATOMIC_INT_SCHEMA = new RuntimePrimitiveSchema<AtomicInteger>(WireFormat.PROTO_ID_INT,
			AtomicInteger.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, AtomicInteger value, IOConfiger<?> conf) {
			conf.getFormat().writeNoTag(value.intValue(), outputStream);
		}

		@Override
		public AtomicInteger readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return new AtomicInteger(tag.getFormat().readInt(inputStream));
		}

	};

	public final static ProtoExSchema<Long> LONG_SCHEMA = new RuntimePrimitiveSchema<Long>(WireFormat.PROTO_ID_LONG, Long.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, Long value, IOConfiger<?> conf) {
			conf.getFormat().writeNoTag(value, outputStream);
		}

		@Override
		public Long readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return tag.getFormat().readLong(inputStream);
		}

	};

	public final static ProtoExSchema<AtomicLong> ATOMIC_LONG_SCHEMA = new RuntimePrimitiveSchema<AtomicLong>(WireFormat.PROTO_ID_LONG,
			AtomicLong.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, AtomicLong value, IOConfiger<?> conf) {
			conf.getFormat().writeNoTag(value.longValue(), outputStream);
		}

		@Override
		public AtomicLong readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return new AtomicLong(tag.getFormat().readLong(inputStream));
		}

	};

	public final static ProtoExSchema<Float> FLOAT_SCHEMA = new RuntimePrimitiveSchema<Float>(WireFormat.PROTO_ID_FLOAT, Float.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, Float value, IOConfiger<?> conf) {
			outputStream.writeFloat(value);
		}

		@Override
		public Float readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return inputStream.readFloat();
		}

	};

	public final static ProtoExSchema<Double> DOUBLE_SCHEMA = new RuntimePrimitiveSchema<Double>(WireFormat.PROTO_ID_DOUBLE, Double.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, Double value, IOConfiger<?> conf) {
			outputStream.writeDouble(value);
		}

		@Override
		public Double readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return inputStream.readDouble();
		}
	};

	public final static ProtoExSchema<Boolean> BOOLEAN_SCHEMA = new RuntimePrimitiveSchema<Boolean>(WireFormat.PROTO_ID_BOOLEAN, Boolean.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, Boolean value, IOConfiger<?> conf) {
			outputStream.writeBoolean(value);
		}

		@Override
		public Boolean readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return inputStream.readBoolean();
		}

	};

	public final static ProtoExSchema<AtomicBoolean> ATOMIC_BOOLEAN_SCHEMA = new RuntimePrimitiveSchema<AtomicBoolean>(WireFormat.PROTO_ID_BOOLEAN,
			AtomicBoolean.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, AtomicBoolean value, IOConfiger<?> conf) {
			outputStream.writeBoolean(value.get());
		}

		@Override
		public AtomicBoolean readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return new AtomicBoolean(inputStream.readBoolean());
		}

	};

	public final static ProtoExSchema<String> STRING_SCHEMA = new RuntimePrimitiveSchema<String>(WireFormat.PROTO_ID_STRING, String.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, String value, IOConfiger<?> conf) {
			outputStream.writeString(value);
		}

		@Override
		public String readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return inputStream.readString();
		}

	};

	public final static ProtoExSchema<byte[]> BYTES_SCHEMA = new RuntimePrimitiveSchema<byte[]>(WireFormat.PROTO_ID_BYTES, byte[].class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, byte[] value, IOConfiger<?> conf) {
			outputStream.writeBytes(value);
		}

		@Override
		public byte[] readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return inputStream.readBytes();
		}

	};

	public final static ProtoExSchema<LinkedBuffer> LINKED_BUFFER_SCHEMA = new RuntimePrimitiveSchema<LinkedBuffer>(WireFormat.PROTO_ID_BYTES,
			LinkedBuffer.class) {

		@Override
		public void writeValue(ProtoExOutputStream outputStream, LinkedBuffer value, IOConfiger<?> conf) {
			outputStream.writeBytes(value);
		}

		@Override
		public LinkedBuffer readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
			return new LinkedBuffer(inputStream.readBytes());
		}

	};

}
