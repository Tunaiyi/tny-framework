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

import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.javassist.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.*;
import org.slf4j.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 自定累类类型描述结构
 *
 * @param <T>
 * @author KGTny
 */
public class RuntimeMessageSchema<T> extends BaseProtoExSchema<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeMessageSchema.class);

    private final ClassAccessor accessor;

    private FieldDesc<?>[] fields;

    private FieldDesc<?>[] fieldsByIndex;

    private Map<Integer, FieldDesc<?>> fieldsByIndexMap;

    public RuntimeMessageSchema(Class<T> typeClass) {
        super(0, false, typeClass.getName());
        this.accessor = JavassistAccessors.getGClass(typeClass);
        ProtoEx proto = typeClass.getAnnotation(ProtoEx.class);
        if (!WireFormat.checkFieldNumber(proto.value())) {
            throw ProtobufExException.invalidProtoExId(typeClass, proto.value());
        }
        this.protoExID = proto.value();
    }

    protected void init(Class<T> typeClass, FieldDesc<?>[] fields, int lastFieldNumber) {
        if (fields.length == 0) {
            throw new IllegalStateException("At least one field is required.");
        }
        this.fields = fields;
        if (lastFieldNumber <= 20) {
            this.fieldsByIndex = new FieldDesc<?>[lastFieldNumber + 1];
            this.fieldsByIndexMap = null;
        } else {
            this.fieldsByIndex = null;
            this.fieldsByIndexMap = new HashMap<>();
        }
        for (FieldDesc<?> field : fields) {
            if (this.fieldsByIndexMap != null) {
                FieldDesc<?> last = this.fieldsByIndexMap.put(field.getIndex(), field);
                if (last != null) {
                    throw new IllegalStateException(format("in {} class, {} and {} cannot have the same index {}", typeClass,
                            field.getName(), last.getName(), field.getIndex()));
                }
            } else if (this.fieldsByIndex != null) {
                if (this.fieldsByIndex[field.getIndex()] != null) {
                    throw new IllegalStateException(format("in {} class, {} and {} cannot have the same index {}", typeClass,
                            field.getName(), this.fieldsByIndex[field.getIndex()].getName(), field.getIndex()));
                }
                this.fieldsByIndex[field.getIndex()] = field;
            }
        }
    }

    public FieldDesc<?> getFieldDesc(int index) {
        if (this.fieldsByIndex != null) {
            if (index < this.fieldsByIndex.length) {
                return this.fieldsByIndex[index];
            }
            return null;
        } else if (this.fieldsByIndexMap != null) {
            return this.fieldsByIndexMap.get(index);
        }
        return null;
    }

    @Override
    public void writeMessage(ProtoExOutputStream outputStream, T value, FieldOptions<?> options) {
        if (value == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("写入 Tag | {} -> IOConfiger : {}", this.accessor.getName(), options);
        }
        this.writeTag(outputStream, options);
        outputStream.writeLengthLimitation(value, options, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeValue(ProtoExOutputStream outputStream, T value, FieldOptions<?> options) {
        for (FieldDesc<?> field : this.fields) {
            FieldDesc<Object> childDesc = (FieldDesc<Object>) field;
            Object fieldValue = childDesc.getValue(value);
            if (fieldValue != null) {
                ProtoExSchemaContext context = outputStream.getSchemaContext();
                ProtoExSchema<Object> schema = context.getSchema(fieldValue.getClass());
                schema.writeMessage(outputStream, fieldValue, childDesc);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T readValue(ProtoExInputStream inputStream, Tag tag, FieldOptions<?> options) {
        T message = null;
        try {
            message = (T) this.accessor.newInstance();
        } catch (InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        int fieldIndex;
        int offset = inputStream.position();
        int length = inputStream.readInt();
        while (inputStream.position() - offset < length) {
            Tag currentTag = inputStream.getTag();
            fieldIndex = currentTag.getFieldNumber();
            //判断是否显示 读取
            final FieldDesc<Object> field = (FieldDesc<Object>) this.getFieldDesc(fieldIndex);
            int protoExID = currentTag.getProtoExId();
            if (field == null) {
                LOGGER.warn("获取 Tag | {} -> [字段ID : {}] 不存在! Tag : {}", this.accessor.getName(), currentTag.getFieldNumber(), currentTag);
                inputStream.readTag();
                inputStream.skipField(currentTag);
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("开始读取 Message | {} -> [字段 : {} - ({})] : {} ", this.accessor.getName(), field.getName(), field.getIndex(),
                            currentTag);
                }
                ProtoExSchemaContext context = inputStream.getSchemaContext();
                ProtoExSchema<Object> schema = context.getSchema(protoExID, currentTag.isRaw(), field.getDefaultType());
                Object value = schema.readMessage(inputStream, field);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("读取到 Message | {} -> [字段 : {} - ({})] : {} ==> {}", this.accessor.getName(), field.getName(), field.getIndex(),
                            currentTag, value);
                }
                if (value != null) {
                    field.setValue(message, value);
                }
            }
        }
        return message;
    }

}
