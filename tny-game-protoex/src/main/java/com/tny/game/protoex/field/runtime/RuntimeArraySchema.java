package com.tny.game.protoex.field.runtime;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.protoex.BaseProtoExSchema;
import com.tny.game.protoex.ProtoExInputStream;
import com.tny.game.protoex.ProtoExOutputStream;
import com.tny.game.protoex.ProtoExSchema;
import com.tny.game.protoex.ProtoExSchemaContext;
import com.tny.game.protoex.ProtobufExException;
import com.tny.game.protoex.Tag;
import com.tny.game.protoex.WireFormat;
import com.tny.game.protoex.field.IOConfiger;
import com.tny.game.protoex.field.RepeatIOConfiger;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public void writeTag(ProtoExOutputStream outputStream, IOConfiger<?> conf) {
        outputStream.writeTag(WireFormat.PROTO_ID_REPEAT, true, true, conf.getIndex(), conf.getFormat());
    }

    @Override
    public void writeMessage(ProtoExOutputStream outputStream, Object array, IOConfiger<?> conf) {
        if (array != null)
            ExceptionUtils.checkArgument(array.getClass().isArray(), "{} 不是 Array", array.getClass());
        if (array == null || Array.getLength(array) == 0)
            return;
        this.writeTag(outputStream, conf);
        outputStream.writeLengthLimitation(array, conf, this);
    }

    @Override
    public void writeValue(ProtoExOutputStream outputStream, Object array, IOConfiger<?> conf) {
        if (array != null)
            ExceptionUtils.checkArgument(array.getClass().isArray(), "{} 不是 Array", array.getClass());
        if (array == null || Array.getLength(array) == 0)
            return;
        if (!(conf instanceof RepeatIOConfiger))
            throw ProtobufExException.notRepeatIOConfiger(conf);
        RepeatIOConfiger<?> repeatIOConf = (RepeatIOConfiger<?>) conf;
        IOConfiger<?> elementDesc = repeatIOConf.getElementConfiger();
        if (conf.isPacked()) {
            ProtoExSchema<Object> elementSchema = outputStream.getSchemaContext().getSchema(elementDesc.getDefaultType());
            this.writePacked(outputStream, array, elementSchema, elementDesc);
        } else {
            this.doWriteUnpacked(outputStream, array, elementDesc);
        }
    }

    private void writePacked(ProtoExOutputStream outputStream, Object array, ProtoExSchema<Object> elementSchema, IOConfiger<?> elementDesc) {
        outputStream.writeInt(WireFormat.makeRepeatOption(elementSchema.getProtoExID(), elementSchema.isRaw(), true));
        int length = Array.getLength(array);
        for (int index = 0; index < length; index++) {
            Object value = Array.get(array, index);
            if (value == null)
                continue;
            if (elementSchema.isRaw()) {
                elementSchema.writeValue(outputStream, value, elementDesc);
            } else {
                outputStream.writeLengthLimitation(value, elementDesc, elementSchema);
            }
        }
    }

    private void doWriteUnpacked(ProtoExOutputStream outputStream, Object array, IOConfiger<?> elementDesc) {
        ProtoExSchemaContext schemaContext = outputStream.getSchemaContext();
        outputStream.writeInt(WireFormat.makeRepeatOption(0, false, false));
        int length = Array.getLength(array);
        for (int index = 0; index < length; index++) {
            Object value = Array.get(array, index);
            if (value == null)
                continue;
            ProtoExSchema<Object> schema = schemaContext.getSchema(value.getClass());
            schema.writeMessage(outputStream, value, elementDesc);
        }
    }

    // TODO 加入参数Class 创建Collection

    @Override
    public Object readMessage(ProtoExInputStream inputStream, IOConfiger<?> conf) {
        Tag tag = this.readTag(inputStream);
        return this.readValue(inputStream, tag, conf);
    }

    @Override
    public Object readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
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
            return this.doReadPackedElements(inputStream, length, elementTag, elementDesc);
        } else {
            return this.doReadUnpackedElements(inputStream, length, elementDesc);
        }
    }

    private Object doReadPackedElements(ProtoExInputStream inputStream, int length, Tag tag, IOConfiger<?> elConf) {
        List<Object> valueList = new ArrayList<>();
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        ProtoExSchema<Object> schema = schemaContext.getSchema(tag.getProtoExID(), tag.isRaw(), elConf.getDefaultType());
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            Object value = schema.readValue(inputStream, tag, elConf);
            if (value != null)
                valueList.add(value);
            position = inputStream.position();
        }
        return toArray(valueList, elConf.getDefaultType());
    }

    private Object doReadUnpackedElements(ProtoExInputStream inputStream, int length, IOConfiger<?> elConf) {
        List<Object> valueList = new ArrayList<>();
        ProtoExSchemaContext schemaContext = inputStream.getSchemaContext();
        int startAt = inputStream.position();
        int position = inputStream.position();
        while (position - startAt < length) {
            Tag targetTag = inputStream.getTag();
            ProtoExSchema<Object> schema = schemaContext.getSchema(targetTag.getProtoExID(), targetTag.isRaw(), elConf.getDefaultType());
            Object value = schema.readMessage(inputStream, elConf);
            if (value != null)
                valueList.add(value);
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
        return valueList.toArray((Object[]) object);
    }

}
