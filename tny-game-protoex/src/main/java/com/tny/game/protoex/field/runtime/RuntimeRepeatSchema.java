package com.tny.game.protoex.field.runtime;

import com.tny.game.protoex.*;
import com.tny.game.protoex.field.IOConfiger;
import com.tny.game.protoex.field.RepeatIOConfiger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 重复(Collection)类型描述结构
 *
 * @author KGTny
 */
public class RuntimeRepeatSchema extends BaseProtoExSchema<Collection<?>> {

    public final static ProtoExSchema<Collection<?>> REPEAT_SCHEMA = new RuntimeRepeatSchema();

    private RuntimeRepeatSchema() {
        super(WireFormat.PROTO_ID_REPEAT, true, Collection.class.getName());
    }

    @Override
    public void writeTag(ProtoExOutputStream outputStream, IOConfiger<?> conf) {
        outputStream.writeTag(WireFormat.PROTO_ID_REPEAT, true, true, conf.getIndex(), conf.getFormat());
    }

    @Override
    public void writeMessage(ProtoExOutputStream outputStream, Collection<?> collection, IOConfiger<?> conf) {
        if (collection == null || collection.isEmpty())
            return;
        this.writeTag(outputStream, conf);
        outputStream.writeLengthLimitation(collection, conf, this);
    }

    @Override
    public void writeValue(ProtoExOutputStream outputStream, Collection<?> collection, IOConfiger<?> conf) {
        if (collection == null || collection.isEmpty())
            return;
        if (!(conf instanceof RepeatIOConfiger))
            throw ProtobufExException.notRepeatIOConfiger(conf);
        RepeatIOConfiger<?> repeatIOConf = (RepeatIOConfiger<?>) conf;
        IOConfiger<?> elementDesc = repeatIOConf.getElementConfiger();
        if (conf.isPacked()) {
            ProtoExSchema<Object> elementSchema = outputStream.getSchemaContext().getSchema(elementDesc.getDefaultType());
            this.writePacked(outputStream, collection, elementSchema, elementDesc);
        } else {
            this.doWriteUnpacked(outputStream, collection, elementDesc);
        }
    }

    private void writePacked(ProtoExOutputStream outputStream, Collection<?> collection, ProtoExSchema<Object> elementSchema, IOConfiger<?> elementDesc) {
        outputStream.writeInt(WireFormat.makeRepeatOption(elementSchema.getProtoExID(), elementSchema.isRaw(), true));
        for (Object value : collection) {
            if (value == null)
                continue;
            if (elementSchema.isRaw()) {
                elementSchema.writeValue(outputStream, value, elementDesc);
            } else {
                outputStream.writeLengthLimitation(value, elementDesc, elementSchema);
            }
        }
    }

    private void doWriteUnpacked(ProtoExOutputStream outputStream, Collection<?> collection, IOConfiger<?> elementDesc) {
        ProtoExSchemaContext schemaContext = outputStream.getSchemaContext();
        outputStream.writeInt(WireFormat.makeRepeatOption(0, false, false));
        for (Object value : collection) {
            if (value == null)
                continue;
            ProtoExSchema<Object> schema = schemaContext.getSchema(value.getClass());
            schema.writeMessage(outputStream, value, elementDesc);
        }
    }

    @Override
    public Collection<?> readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
        int length = inputStream.readInt();
        if (length < 0)
            throw ProtobufExException.negativeSize(length);

        if (!(conf instanceof RepeatIOConfiger))
            throw ProtobufExException.notRepeatIOConfiger(conf);
        RepeatIOConfiger<?> repeatIOConf = (RepeatIOConfiger<?>) conf;
        IOConfiger<?> elementDesc = repeatIOConf.getElementConfiger();

        int offset = inputStream.position();
        int repeatOption = inputStream.readInt();
        int optionSize = inputStream.position() - offset;
        length = length - optionSize;
        if (length < 0)
            throw ProtobufExException.negativeSize(length);

        boolean packed = WireFormat.isRepeatPacked(repeatOption);
        int repeatChildID = WireFormat.getRepeatChildID(repeatOption);
        boolean raw = WireFormat.isRepeatChildRaw(repeatOption);
        Tag elementTag = new Tag(repeatChildID, raw, tag);

        if (packed) {
            return this.doReadPackeds(inputStream, length, elementTag, elementDesc);
        } else {
            return this.doReadUnpackeds(inputStream, length, elementTag, elementDesc);
        }
    }

    private <T, C extends Collection<T>> Collection<T> doReadPackeds(ProtoExInputStream inputStream, int length, Tag tag, IOConfiger<?> elConf) {
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        ProtoExSchema<T> schema = schemaContext.getSchema(tag.getProtoExID(), tag.isRaw(), elConf.getDefaultType());
        List<T> valueList = new ArrayList<T>();
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            T value = schema.readValue(inputStream, tag, elConf);
            if (value != null)
                valueList.add(value);
            position = inputStream.position();
        }
        return valueList;
    }

    private <T, C extends Collection<T>> Collection<T> doReadUnpackeds(ProtoExInputStream inputStream, int length, Tag tag, IOConfiger<?> elConf) {
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        List<T> valueList = new ArrayList<T>();
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            Tag targetTag = inputStream.getTag();
            ProtoExSchema<T> schema = schemaContext.getSchema(targetTag.getProtoExID(), targetTag.isRaw(), elConf.getDefaultType());
            T value = schema.readMessage(inputStream, elConf);
            if (value != null)
                valueList.add(value);
            position = inputStream.position();
        }
        return valueList;
    }

}