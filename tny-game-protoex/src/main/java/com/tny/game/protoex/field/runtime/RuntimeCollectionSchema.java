package com.tny.game.protoex.field.runtime;

import com.tny.game.protoex.*;
import com.tny.game.protoex.field.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * 重复(Collection)类型描述结构
 *
 * @author KGTny
 */
public class RuntimeCollectionSchema extends BaseProtoExSchema<Collection<?>> {

    public final static RuntimeCollectionSchema COLLECTION_SCHEMA = new RuntimeCollectionSchema();

    private RuntimeCollectionSchema() {
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

    private void writePacked(ProtoExOutputStream outputStream, Collection<?> collection, ProtoExSchema<Object> elementSchema,
            IOConfiger<?> elementDesc) {
        outputStream.writeInt(WireFormat.makeRepeatOption(elementSchema.getProtoExId(), elementSchema.isRaw(), true));
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

    // TODO 加入参数Class 创建Collection

    public <T, C extends Collection<T>> C readMessage(Supplier<C> supplier, ProtoExInputStream inputStream, IOConfiger<?> conf) {
        Tag tag = this.readTag(inputStream);
        return this.readValue(supplier.get(), inputStream, tag, conf);
    }

    @Override
    public Collection<?> readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
        if (conf == null)
            conf = ProtoExIO.createRepeat(ArrayList.class, Object.class, false);
        return readValue(CollectionCreator.createCollection(conf.getDefaultType()), inputStream, tag, conf);
    }

    public <T, C extends Collection<T>> C readValue(C valueList, ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
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
        int repeatChildID = WireFormat.getRepeatChildId(repeatOption);
        boolean raw = WireFormat.isRepeatChildRaw(repeatOption);
        Tag elementTag = new Tag(repeatChildID, raw, tag);
        if (packed) {
            return this.doReadPackedElements(valueList, inputStream, length, elementTag, elementDesc);
        } else {
            return this.doReadUnpackedElements(valueList, inputStream, length, elementDesc);
        }
    }

    private <T, C extends Collection<T>> C doReadPackedElements(C valueList, ProtoExInputStream inputStream, int length, Tag tag,
            IOConfiger<?> elConf) {
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        ProtoExSchema<T> schema = schemaContext.getSchema(tag.getProtoExId(), tag.isRaw(), elConf.getDefaultType());
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

    private <T, C extends Collection<T>> C doReadUnpackedElements(C valueList, ProtoExInputStream inputStream, int length, IOConfiger<?> elConf) {
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            Tag targetTag = inputStream.getTag();
            ProtoExSchema<T> schema = schemaContext.getSchema(targetTag.getProtoExId(), targetTag.isRaw(), elConf.getDefaultType());
            T value = schema.readMessage(inputStream, elConf);
            if (value != null)
                valueList.add(value);
            position = inputStream.position();
        }
        return valueList;
    }

}
