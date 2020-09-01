package com.tny.game.protoex.field;

import com.tny.game.common.type.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 跟对象编码方式
 *
 * @param <T>
 * @author KGTny
 */
public class RootIOConfiger<T> extends BaseIOConfiger<T> implements MapIOConfiger<T>, RepeatIOConfiger<T> {

    private IOConfiger<?> elementConfiger;
    private IOConfiger<?> keyConfiger;
    private IOConfiger<?> valueConfiger;

    public static <T> IOConfiger<T> createNormalConfiger(ProtoExType protoExType, T type, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        return new RootIOConfiger<>(protoExType, type, packed, typeEncode, format);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Collection<?>> RootIOConfiger<C> createRepeatConfiger(Class<C> collectionClass, Class<?> elementType, boolean packed,
            TypeEncode elTypeEncode, FieldFormat elFormat) {
        return new RootIOConfiger<>(collectionClass, elementType, packed, elTypeEncode, elFormat);
    }

    @SuppressWarnings("unchecked")
    public static IOConfiger<Map<?, ?>> createMapConfiger(
            Class<?> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
            Class<?> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        Class<Map<?, ?>> type = (Class<Map<?, ?>>)Collections.emptyMap().getClass();
        return new RootIOConfiger<>(type, keyType, keyTypeEncode, keyFormat, valueType, valueTypeEncode, valueFormat);
    }

    @SuppressWarnings({"unchecked"})
    private RootIOConfiger(Class<T> type,
            Class<?> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
            Class<?> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        this(ProtoExType.REPEAT, type, false, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
        Class<Object> keyClass = (Class<Object>)keyType;
        Class<Object> valueClass = (Class<Object>)valueType;
        this.keyConfiger = new SimpleIOConfiger<>(EntryType.KEY, keyClass, keyTypeEncode, keyFormat);
        this.valueConfiger = new SimpleIOConfiger<>(EntryType.VALUE, valueClass, valueTypeEncode, valueFormat);
    }

    @SuppressWarnings({"unchecked",})
    private RootIOConfiger(Class<T> clazz, Class<?> elementType, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        this(ProtoExType.REPEAT, clazz, packed, TypeEncode.DEFAULT, format);
        boolean primitive = Wrapper.getPrimitive(elementType).isPrimitive();
        this.elementConfiger = new SimpleIOConfiger<>((Class<Object>)elementType, this.getIndex(), typeEncode, format);
        this.packed = packed;
        if (primitive) {
            this.packed = true;
        } else if (Modifier.isAbstract(elementType.getModifiers()) || elementType == Object.class) {
            this.packed = false;
        }
    }

    @SuppressWarnings("unchecked")
    private RootIOConfiger(ProtoExType protoExType, T type, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        this(protoExType, (Class<T>)type.getClass(), packed, typeEncode, format);
    }

    private RootIOConfiger(ProtoExType protoExType, Class<T> type, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        super(protoExType, type, "root", 0, packed, typeEncode, format);
    }

    @Override
    public IOConfiger<?> getKeyConfiger() {
        return this.keyConfiger;
    }

    @Override
    public IOConfiger<?> getValueConfiger() {
        return this.valueConfiger;
    }

    @Override
    public IOConfiger<?> getElementConfiger() {
        return this.elementConfiger;
    }

}
