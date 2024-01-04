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
    public void writeTag(ProtoExOutputStream outputStream, FieldOptions<?> options) {
        outputStream.writeTag(WireFormat.PROTO_ID_REPEAT, true, true, options.getIndex(), options.getFormat());
    }

    @Override
    public void writeMessage(ProtoExOutputStream outputStream, Collection<?> collection, FieldOptions<?> options) {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        this.writeTag(outputStream, options);
        outputStream.writeLengthLimitation(collection, options, this);
    }

    @Override
    public void writeValue(ProtoExOutputStream outputStream, Collection<?> collection, FieldOptions<?> options) {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        if (!(options instanceof RepeatFieldOptions)) {
            throw ProtobufExException.notRepeatOptions(options);
        }
        RepeatFieldOptions<?> repeatIOConf = (RepeatFieldOptions<?>) options;
        FieldOptions<?> elementDesc = repeatIOConf.getElementOptions();
        if (options.isPacked()) {
            ProtoExSchema<Object> elementSchema = outputStream.getSchemaContext().getSchema(elementDesc.getDefaultType());
            this.writePacked(outputStream, collection, elementSchema, elementDesc);
        } else {
            this.doWriteUnpacked(outputStream, collection, elementDesc);
        }
    }

    private void writePacked(ProtoExOutputStream outputStream, Collection<?> collection, ProtoExSchema<Object> elementSchema,
            FieldOptions<?> elementDesc) {
        outputStream.writeInt(WireFormat.makeRepeatOption(elementSchema.getProtoExId(), elementSchema.isRaw(), true));
        for (Object value : collection) {
            if (value == null) {
                continue;
            }
            if (elementSchema.isRaw()) {
                elementSchema.writeValue(outputStream, value, elementDesc);
            } else {
                outputStream.writeLengthLimitation(value, elementDesc, elementSchema);
            }
        }
    }

    private void doWriteUnpacked(ProtoExOutputStream outputStream, Collection<?> collection, FieldOptions<?> elementDesc) {
        ProtoExSchemaContext schemaContext = outputStream.getSchemaContext();
        outputStream.writeInt(WireFormat.makeRepeatOption(0, false, false));
        for (Object value : collection) {
            if (value == null) {
                continue;
            }
            ProtoExSchema<Object> schema = schemaContext.getSchema(value.getClass());
            schema.writeMessage(outputStream, value, elementDesc);
        }
    }

    // TODO 加入参数Class 创建Collection

    public <T, C extends Collection<T>> C readMessage(Supplier<C> supplier, ProtoExInputStream inputStream, FieldOptions<?> options) {
        Tag tag = this.readTag(inputStream);
        return this.readValue(supplier.get(), inputStream, tag, options);
    }

    @Override
    public Collection<?> readValue(ProtoExInputStream inputStream, Tag tag, FieldOptions<?> options) {
        if (options == null) {
            options = ProtoExIO.createRepeat(ArrayList.class, Object.class, false);
        }
        return readValue(CollectionCreator.createCollection(options.getDefaultType()), inputStream, tag, options);
    }

    public <T, C extends Collection<T>> C readValue(C valueList, ProtoExInputStream inputStream, Tag tag, FieldOptions<?> options) {
        int length = inputStream.readInt();
        if (length < 0) {
            throw ProtobufExException.negativeSize(length);
        }

        if (!(options instanceof RepeatFieldOptions)) {
            throw ProtobufExException.notRepeatOptions(options);
        }
        RepeatFieldOptions<?> repeatIOConf = (RepeatFieldOptions<?>) options;
        FieldOptions<?> elementDesc = repeatIOConf.getElementOptions();

        int offset = inputStream.position();
        int repeatOption = inputStream.readInt();
        int optionSize = inputStream.position() - offset;
        length = length - optionSize;
        if (length < 0) {
            throw ProtobufExException.negativeSize(length);
        }

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
            FieldOptions<?> elConf) {
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        ProtoExSchema<T> schema = schemaContext.getSchema(tag.getProtoExId(), tag.isRaw(), elConf.getDefaultType());
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            T value = schema.readValue(inputStream, tag, elConf);
            if (value != null) {
                valueList.add(value);
            }
            position = inputStream.position();
        }
        return valueList;
    }

    private <T, C extends Collection<T>> C doReadUnpackedElements(C valueList, ProtoExInputStream inputStream, int length,
            FieldOptions<?> elConf) {
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            Tag targetTag = inputStream.getTag();
            ProtoExSchema<T> schema = schemaContext.getSchema(targetTag.getProtoExId(), targetTag.isRaw(), elConf.getDefaultType());
            T value = schema.readMessage(inputStream, elConf);
            if (value != null) {
                valueList.add(value);
            }
            position = inputStream.position();
        }
        return valueList;
    }

}
