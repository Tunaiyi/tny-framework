/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex.field.runtime;

import com.tny.game.common.utils.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.field.*;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 重复(Collection)类型描述结构
 *
 * @author KGTny
 */
public class RuntimeArraySchema extends BaseProtoExSchema<Object> {

    public final static RuntimeArraySchema ARRAY_SCHEMA = new RuntimeArraySchema();

    private RuntimeArraySchema() {
        super(WireFormat.PROTO_ID_REPEAT, true, Collection.class.getName());
    }

    @Override
    public void writeTag(ProtoExOutputStream outputStream, FieldOptions<?> options) {
        outputStream.writeTag(WireFormat.PROTO_ID_REPEAT, true, true, options.getIndex(), options.getFormat());
    }

    @Override
    public void writeMessage(ProtoExOutputStream outputStream, Object array, FieldOptions<?> options) {
        if (array != null) {
            Asserts.checkArgument(array.getClass().isArray(), "{} 不是 Array", array.getClass());
        }
        if (array == null || Array.getLength(array) == 0) {
            return;
        }
        this.writeTag(outputStream, options);
        outputStream.writeLengthLimitation(array, options, this);
    }

    @Override
    public void writeValue(ProtoExOutputStream outputStream, Object array, FieldOptions<?> options) {
        if (array != null) {
            Asserts.checkArgument(array.getClass().isArray(), "{} 不是 Array", array.getClass());
        }
        if (array == null || Array.getLength(array) == 0) {
            return;
        }
        if (!(options instanceof RepeatFieldOptions)) {
            throw ProtobufExException.notRepeatOptions(options);
        }
        RepeatFieldOptions<?> repeatIOConf = (RepeatFieldOptions<?>)options;
        FieldOptions<?> elementDesc = repeatIOConf.getElementOptions();
        if (options.isPacked()) {
            ProtoExSchema<Object> elementSchema = outputStream.getSchemaContext().getSchema(elementDesc.getDefaultType());
            this.writePacked(outputStream, array, elementSchema, elementDesc);
        } else {
            this.doWriteUnpacked(outputStream, array, elementDesc);
        }
    }

    private void writePacked(ProtoExOutputStream outputStream, Object array, ProtoExSchema<Object> elementSchema, FieldOptions<?> elementDesc) {
        outputStream.writeInt(WireFormat.makeRepeatOption(elementSchema.getProtoExId(), elementSchema.isRaw(), true));
        int length = Array.getLength(array);
        for (int index = 0; index < length; index++) {
            Object value = Array.get(array, index);
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

    private void doWriteUnpacked(ProtoExOutputStream outputStream, Object array, FieldOptions<?> elementDesc) {
        ProtoExSchemaContext schemaContext = outputStream.getSchemaContext();
        outputStream.writeInt(WireFormat.makeRepeatOption(0, false, false));
        int length = Array.getLength(array);
        for (int index = 0; index < length; index++) {
            Object value = Array.get(array, index);
            if (value == null) {
                continue;
            }
            ProtoExSchema<Object> schema = schemaContext.getSchema(value.getClass());
            schema.writeMessage(outputStream, value, elementDesc);
        }
    }

    // TODO 加入参数Class 创建Collection

    @Override
    public Object readMessage(ProtoExInputStream inputStream, FieldOptions<?> options) {
        Tag tag = this.readTag(inputStream);
        return this.readValue(inputStream, tag, options);
    }

    @Override
    public Object readValue(ProtoExInputStream inputStream, Tag tag, FieldOptions<?> options) {
        int length = inputStream.readInt();
        if (length < 0) {
            throw ProtobufExException.negativeSize(length);
        }

        if (!(options instanceof RepeatFieldOptions)) {
            throw ProtobufExException.notRepeatOptions(options);
        }
        RepeatFieldOptions<?> repeatIOConf = (RepeatFieldOptions<?>)options;
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
            return this.doReadPackedElements(inputStream, length, elementTag, elementDesc);
        } else {
            return this.doReadUnpackedElements(inputStream, length, elementDesc);
        }
    }

    private Object doReadPackedElements(ProtoExInputStream inputStream, int length, Tag tag, FieldOptions<?> elConf) {
        List<Object> valueList = new ArrayList<>();
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        ProtoExSchema<Object> schema = schemaContext.getSchema(tag.getProtoExId(), tag.isRaw(), elConf.getDefaultType());
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            Object value = schema.readValue(inputStream, tag, elConf);
            if (value != null) {
                valueList.add(value);
            }
            position = inputStream.position();
        }
        return toArray(valueList, elConf.getDefaultType());
    }

    private Object doReadUnpackedElements(ProtoExInputStream inputStream, int length, FieldOptions<?> elConf) {
        List<Object> valueList = new ArrayList<>();
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            Tag targetTag = inputStream.getTag();
            ProtoExSchema<Object> schema = schemaContext.getSchema(targetTag.getProtoExId(), targetTag.isRaw(), elConf.getDefaultType());
            Object value = schema.readMessage(inputStream, elConf);
            if (value != null) {
                valueList.add(value);
            }
            position = inputStream.position();
        }
        return toArray(valueList, elConf.getDefaultType());
    }

    private Object toArray(List<Object> valueList, Class<?> elClass) {
        if (elClass.isPrimitive()) {
            if (elClass == byte.class) {
                return ArrayUtils.toPrimitive(valueList.toArray(new Byte[valueList.size()]));
            } else if (elClass == short.class) {
                return ArrayUtils.toPrimitive(valueList.toArray(new Short[valueList.size()]));
            } else if (elClass == int.class) {
                return ArrayUtils.toPrimitive(valueList.toArray(new Integer[valueList.size()]));
            } else if (elClass == long.class) {
                return ArrayUtils.toPrimitive(valueList.toArray(new Long[valueList.size()]));
            } else if (elClass == float.class) {
                return ArrayUtils.toPrimitive(valueList.toArray(new Float[valueList.size()]));
            } else if (elClass == double.class) {
                return ArrayUtils.toPrimitive(valueList.toArray(new Double[valueList.size()]));
            } else if (elClass == boolean.class) {
                return ArrayUtils.toPrimitive(valueList.toArray(new Boolean[valueList.size()]));
            }
        }
        Object object = Array.newInstance(elClass, valueList.size());
        return valueList.toArray((Object[])object);
    }

}
