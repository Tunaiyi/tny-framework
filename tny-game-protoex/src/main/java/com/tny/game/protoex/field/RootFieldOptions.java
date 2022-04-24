package com.tny.game.protoex.field;

import com.tny.game.common.type.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;

import java.lang.reflect.Modifier;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 跟对象编码方式
 *
 * @param <T>
 * @author KGTny
 */
public class RootFieldOptions<T> extends BaseFieldOptions<T> implements MapFieldOptions<T>, RepeatFieldOptions<T> {

    private FieldOptions<?> elementOptions;

    private FieldOptions<?> keyOptions;

    private FieldOptions<?> valueOptions;

    public static <T> FieldOptions<T> createNormalOptions(ProtoExType protoExType, T type, boolean packed, TypeEncode typeEncode,
            FieldFormat format) {
        return new RootFieldOptions<>(protoExType, type, packed, typeEncode, format);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Collection<?>> RootFieldOptions<C> createRepeatOptions(Class<C> collectionClass, Class<?> elementType,
            boolean packed,
            TypeEncode elTypeEncode, FieldFormat elFormat) {
        return new RootFieldOptions<>(collectionClass, elementType, packed, elTypeEncode, elFormat);
    }

    @SuppressWarnings("unchecked")
    public static <C> RootFieldOptions<C> createArrayOptions(Class<C> arrayClass, Class<?> elementType, boolean packed, TypeEncode elTypeEncode,
            FieldFormat elFormat) {
        if (!arrayClass.isArray()) {
            throw new ClassCastException(format("{} not array class", arrayClass));
        }
        if (elementType == null) {
            elementType = arrayClass.getComponentType();
        }
        return new RootFieldOptions<>(arrayClass, elementType, packed, elTypeEncode, elFormat);
    }

    @SuppressWarnings("unchecked")
    public static FieldOptions<Map<?, ?>> createMapOptions(
            Class<?> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
            Class<?> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        Class<Map<?, ?>> type = (Class<Map<?, ?>>)Collections.emptyMap().getClass();
        return new RootFieldOptions<>(type, keyType, keyTypeEncode, keyFormat, valueType, valueTypeEncode, valueFormat);
    }

    @SuppressWarnings({"unchecked"})
    private RootFieldOptions(Class<T> type,
            Class<?> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
            Class<?> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        this(ProtoExType.REPEAT, type, false, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
        Class<Object> keyClass = (Class<Object>)keyType;
        Class<Object> valueClass = (Class<Object>)valueType;
        this.keyOptions = new SimpleFieldOptions<>(EntryType.KEY, keyClass, keyTypeEncode, keyFormat);
        this.valueOptions = new SimpleFieldOptions<>(EntryType.VALUE, valueClass, valueTypeEncode, valueFormat);
    }

    @SuppressWarnings({"unchecked",})
    private RootFieldOptions(Class<T> clazz, Class<?> elementType, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        this(ProtoExType.REPEAT, clazz, packed, TypeEncode.DEFAULT, format);
        boolean primitive = Wrapper.getPrimitive(elementType).isPrimitive();
        this.elementOptions = new SimpleFieldOptions<>((Class<Object>)elementType, this.getIndex(), typeEncode, format);
        this.packed = packed;
        if (primitive) {
            this.packed = true;
        } else if (Modifier.isAbstract(elementType.getModifiers()) || elementType == Object.class) {
            this.packed = false;
        }
    }

    @SuppressWarnings("unchecked")
    private RootFieldOptions(ProtoExType protoExType, T type, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        this(protoExType, (Class<T>)type.getClass(), packed, typeEncode, format);
    }

    private RootFieldOptions(ProtoExType protoExType, Class<T> type, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        super(protoExType, type, "root", 0, packed, typeEncode, format);
    }

    @Override
    public FieldOptions<?> getKeyOptions() {
        return this.keyOptions;
    }

    @Override
    public FieldOptions<?> getValueOptions() {
        return this.valueOptions;
    }

    @Override
    public FieldOptions<?> getElementOptions() {
        return this.elementOptions;
    }

}
