package com.tny.game.protoex.field.runtime;

import com.tny.game.LogUtils;
import com.tny.game.common.reflect.GClass;
import com.tny.game.common.reflect.javassist.JSsistUtils;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.field.FieldDesc;
import com.tny.game.protoex.field.IOConfiger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定累类类型描述结构
 *
 * @param <T>
 * @author KGTny
 */
public class RuntimeMessageSchema<T> extends BaseProtoExSchema<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RuntimeMessageSchema.class);
    private GClass gClass;
    protected FieldDesc<?>[] fields;
    protected FieldDesc<?>[] fieldsByIndex;
    protected Map<Integer, FieldDesc<?>> fieldsByIndexMap;

    public RuntimeMessageSchema(Class<T> typeClass) {
        super(0, false, typeClass.getName());
        this.gClass = JSsistUtils.getGClass(typeClass);
        ProtoEx proto = typeClass.getAnnotation(ProtoEx.class);
        if (WireFormat.checkFieldNumber(proto.value()))
            throw ProtobufExException.invalidProtoExID(typeClass, proto.value());
        this.protoExID = proto.value();
    }

    protected void init(Class<T> typeClass, FieldDesc<?>[] fields, int lastFieldNumber) {
        if (fields.length == 0)
            throw new IllegalStateException("At least one field is required.");
        this.fields = fields;
        if (lastFieldNumber <= 20) {
            this.fieldsByIndex = new FieldDesc<?>[lastFieldNumber + 1];
            this.fieldsByIndexMap = null;
        } else {
            this.fieldsByIndex = null;
            this.fieldsByIndexMap = new HashMap<Integer, FieldDesc<?>>();
        }
        for (FieldDesc<?> field : fields) {
            if (this.fieldsByIndexMap != null) {
                FieldDesc<?> last = this.fieldsByIndexMap.put(field.getIndex(), field);
                if (last != null) {
                    throw new IllegalStateException(LogUtils.format("in {} class, {} and {} cannot have the same index {}", typeClass,
                            field.getName(), last.getName(), field.getIndex()));
                }
            } else if (this.fieldsByIndex != null) {
                if (this.fieldsByIndex[field.getIndex()] != null) {
                    throw new IllegalStateException(LogUtils.format("in {} class, {} and {} cannot have the same index {}", typeClass,
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
    public int getProtoExID() {
        return this.protoExID;
    }

    @Override
    public void writeMessage(ProtoExOutputStream outputStream, T value, IOConfiger<?> conf) {
        if (value == null)
            return;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("写入 Tag | {} -> IOConfiger : {}", this.gClass.getName(), conf);
        this.writeTag(outputStream, conf);
        outputStream.writeLengthLimitation(value, conf, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeValue(ProtoExOutputStream outputStream, T value, IOConfiger<?> conf) {
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
    public T readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
        T message = null;
        try {
            message = (T) this.gClass.newInstance();
        } catch (InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        int fieldIndex = 1;
        int offset = inputStream.position();
        int length = inputStream.readInt();
        while (inputStream.position() - offset < length) {
            Tag currentTag = inputStream.getTag();
            fieldIndex = currentTag.getFieldNumber();
            //判断是否显示 读取
            final FieldDesc<Object> field = (FieldDesc<Object>) this.getFieldDesc(fieldIndex);
            int protoExID = currentTag.getProtoExID();
            if (field == null) {
                LOGGER.warn("获取 Tag | {} -> [字段ID : {}] 不存在! Tag : {}", this.gClass.getName(), currentTag.getFieldNumber(), currentTag);
                inputStream.readTag();
                inputStream.skipField(currentTag);
            } else {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("开始读取 Message | {} -> [字段 : {} - ({})] : {} ", this.gClass.getName(), field.getName(), field.getIndex(), currentTag);
                ProtoExSchemaContext context = inputStream.getSchemaContext();
                ProtoExSchema<Object> schema = context.getSchema(protoExID, currentTag.isRaw(), field.getDefaultType());
                Object value = schema.readMessage(inputStream, field);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("读取到 Message | {} -> [字段 : {} - ({})] : {} ==> {}", this.gClass.getName(), field.getName(), field.getIndex(), currentTag, value);
                if (value != null)
                    field.setValue(message, value);
            }
        }
        return message;
    }
}
