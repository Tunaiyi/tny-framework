package com.tny.game.protoex.field.runtime;

import com.tny.game.LogUtils;
import com.tny.game.protoex.ProtoExInputStream;
import com.tny.game.protoex.ProtoExOutputStream;
import com.tny.game.protoex.Tag;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.field.IOConfiger;

/**
 * Enum类型描述结构
 *
 * @param <E>
 * @author KGTny
 */
public class EnumSchema<E extends Enum<E>> extends RuntimePrimitiveSchema<E> {

    private Class<E> typeClass;

    protected EnumSchema(Class<E> clazz) {
        super(0, clazz);
        this.typeClass = clazz;
        ProtoEx proto = this.typeClass.getAnnotation(ProtoEx.class);
        if (proto == null)
            throw new RuntimeException(LogUtils.format("{} @{} is null", this.typeClass, ProtoEx.class));
        this.protoExID = proto.value();
        this.raw = false;
    }

    @Override
    public void writeValue(ProtoExOutputStream outputStream, E value, IOConfiger<?> conf) {
        outputStream.writeEnum(value);
    }

    @Override
    public E readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf) {
        return inputStream.readEnum(this.typeClass);
    }

}
