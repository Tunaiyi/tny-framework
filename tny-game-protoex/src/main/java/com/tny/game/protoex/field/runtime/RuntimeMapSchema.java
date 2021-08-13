package com.tny.game.protoex.field.runtime;

import com.tny.game.protoex.*;
import com.tny.game.protoex.field.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * Map类型描述结构
 *
 * @author KGTny
 */
public class RuntimeMapSchema extends BaseProtoExSchema<Map<?, ?>> {

	public final static ProtoExSchema<Map<?, ?>> MAP_SCHEMA = new RuntimeMapSchema();

	private RuntimeMapSchema() {
		super(WireFormat.PROTO_ID_MAP, true, Map.class.getName());
	}

	@Override
	public void writeMessage(ProtoExOutputStream outputStream, Map<?, ?> map, FieldOptions<?> options) {
		if (map == null || map.isEmpty()) {
			return;
		}
		this.writeTag(outputStream, options);
		outputStream.writeLengthLimitation(map, options, this);
	}

	@Override
	public void writeValue(ProtoExOutputStream outputStream, Map<?, ?> map, FieldOptions<?> options) {
		if (map == null || map.isEmpty()) {
			return;
		}
		if (!(options instanceof MapFieldOptions)) {
			throw ProtobufExException.notMapOptions(options);
		}
		MapFieldOptions<?> mapConf = (MapFieldOptions<?>)options;
		FieldOptions<?> keyDesc = mapConf.getKeyOptions();
		FieldOptions<?> valueDesc = mapConf.getValueOptions();
		for (Entry<?, ?> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key != null) {
				if (!this.writeEntry(outputStream, key, keyDesc)) {
					continue;
				}
				if (value == null) {
					continue;
				}
				this.writeEntry(outputStream, value, valueDesc);
			}
		}
	}

	private boolean writeEntry(ProtoExOutputStream outputStream, Object value, FieldOptions<?> options) {
		if (value == null) {
			return false;
		}
		ProtoExSchemaContext schemaContext = outputStream.getSchemaContext();
		ProtoExSchema<Object> schema = schemaContext.getSchema(value.getClass());
		schema.writeMessage(outputStream, value, options);
		return true;
	}

	@Override
	public Map<?, ?> readValue(ProtoExInputStream inputStream, Tag tag, FieldOptions<?> options) {
		long length = inputStream.readInt();
		if (length < 0) {
			throw ProtobufExException.negativeSize(length);
		}

		if (!(options instanceof MapFieldOptions)) {
			throw ProtobufExException.notMapOptions(options);
		}
		MapFieldOptions<?> mapConf = (MapFieldOptions<?>)options;
		FieldOptions<?> keyDesc = mapConf.getKeyOptions();
		FieldOptions<?> valueDesc = mapConf.getValueOptions();
		int startAt = inputStream.position();
		int current = startAt;
		Map<Object, Object> map = new HashMap<Object, Object>();
		while (current - startAt < length) {
			Object key = null;
			Object value = null;

			Tag keyTag = inputStream.getTag();
			if (EntryType.KEY.isType(keyTag)) {
				key = this.readEntry(inputStream, keyTag, keyDesc);
			}

			Tag valueTag = inputStream.getTag();
			if (EntryType.VALUE.isType(valueTag)) {
				value = this.readEntry(inputStream, valueTag, valueDesc);
			}

			if (key != null) {
				map.put(key, value);
			}
			current = inputStream.position();
		}
		return map;
	}

	private Object readEntry(ProtoExInputStream inputStream, Tag tag, FieldOptions<?> options) {
		ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
		ProtoExSchema<Object> schema = schemaContext.getSchema(tag.getProtoExId(), tag.isRaw(), options.getDefaultType());
		return schema.readMessage(inputStream, options);
	}

}
