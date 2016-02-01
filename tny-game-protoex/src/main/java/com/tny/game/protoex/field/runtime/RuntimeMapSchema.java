package com.tny.game.protoex.field.runtime;

import com.tny.game.protoex.*;
import com.tny.game.protoex.field.EntryType;
import com.tny.game.protoex.field.IOConfiger;
import com.tny.game.protoex.field.MapIOConfiger;

import java.util.HashMap;
import java.util.Map;
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
    public void writeMessage(ProtoExOutputStream outputStream, Map<?, ?> map, IOConfiger<?> conf) {
        if (map == null || map.isEmpty())
            return;
        this.writeTag(outputStream, conf);
        outputStream.writeLengthLimitation(map, conf, this);
    }

    @Override
    public void writeValue(ProtoExOutputStream outputStream, Map<?, ?> map, IOConfiger<?> conf) {
        if (map == null || map.isEmpty())
            return;
        if (!(conf instanceof MapIOConfiger))
            throw ProtobufExException.notMapIOConfiger(conf);
        MapIOConfiger<?> mapConf = (MapIOConfiger<?>) conf;
        IOConfiger<?> keyDesc = mapConf.getKeyConfiger();
        IOConfiger<?> valueDesc = mapConf.getValueConfiger();
        for (Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key != null) {
                if (!this.writeEntry(outputStream, key, keyDesc))
                    continue;
                if (value == null)
                    continue;
                this.writeEntry(outputStream, value, valueDesc);
            }
        }
    }

    private boolean writeEntry(ProtoExOutputStream outputStream, Object value, IOConfiger<?> conf) {
        if (value == null)
            return false;
        ProtoExSchemaContext schemaContext = outputStream.getSchemaContext();
        ProtoExSchema<Object> schema = schemaContext.getSchema(value.getClass());
        schema.writeMessage(outputStream, value, conf);
        return true;
    }

    @Override
    public Map<?, ?> readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
        long length = inputStream.readInt();
        if (length < 0)
            throw ProtobufExException.negativeSize(length);

        if (!(conf instanceof MapIOConfiger))
            throw ProtobufExException.notMapIOConfiger(conf);
        MapIOConfiger<?> mapConf = (MapIOConfiger<?>) conf;
        IOConfiger<?> keyDesc = mapConf.getKeyConfiger();
        IOConfiger<?> valueDesc = mapConf.getValueConfiger();
        int startAt = inputStream.position();
        int current = startAt;
        Map<Object, Object> map = new HashMap<Object, Object>();
        while (current - startAt < length) {
            Object key = null;
            Object value = null;

            Tag keyTag = inputStream.getTag();
            if (EntryType.KEY.isType(keyTag))
                key = this.readEntry(inputStream, keyTag, keyDesc);

            Tag valueTag = inputStream.getTag();
            if (EntryType.VALUE.isType(valueTag))
                value = this.readEntry(inputStream, valueTag, valueDesc);

            if (key != null)
                map.put(key, value);
            current = inputStream.position();
        }
        return map;
    }

    private Object readEntry(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        ProtoExSchema<Object> schema = schemaContext.getSchema(tag.getProtoExID(), tag.isRaw(), conf.getDefaultType());
        return schema.readMessage(inputStream, conf);
    }

}
