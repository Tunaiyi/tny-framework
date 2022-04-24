package com.tny.game.protoex;

import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.*;

import java.util.*;

public class ProtoExIO {

    public static <T> ProtoExSchema<T> getSchema(ProtoExStream outputStream, Class<?> value) {
        return outputStream.getSchemaContext().getSchema(value);
    }

    public static <T> ProtoExSchema<T> getSchema(ProtoExInputStream inputStream, Class<T> clazz, Tag tag) {
        return inputStream.getSchemaContext().getSchema(tag.getProtoExId(), tag.isRaw(), clazz);
    }

    public static <T> FieldOptions<T> createNormal(ProtoExType protoExType, T type) {
        return createNormal(protoExType, type, false, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public static <T> FieldOptions<T> createNormal(ProtoExType protoExType, T type, TypeEncode typeEncode) {
        return createNormal(protoExType, type, false, typeEncode, FieldFormat.DEFAULT);
    }

    public static <T> FieldOptions<T> createNormal(ProtoExType protoExType, T type, FieldFormat format) {
        return createNormal(protoExType, type, false, TypeEncode.DEFAULT, format);
    }

    public static <T> FieldOptions<T> createNormal(ProtoExType protoExType, T type, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        return RootFieldOptions.createNormalOptions(protoExType, type, packed, typeEncode, format);
    }

    public static <C extends Collection<?>> RootFieldOptions<C> createArray(Class<C> arrayClass, boolean packed,
            TypeEncode elTypeEncode, FieldFormat elFormat) {
        return RootFieldOptions.createArrayOptions(arrayClass, null, packed, elTypeEncode, elFormat);
    }

    public static <C extends Collection<?>> RootFieldOptions<C> createArray(Class<C> arrayClass, boolean packed) {
        return RootFieldOptions.createArrayOptions(arrayClass, null, packed, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public static <C extends Collection<?>> RootFieldOptions<C> createArray(Class<C> arrayClass, Class<?> elementType, boolean packed,
            TypeEncode elTypeEncode, FieldFormat elFormat) {
        return RootFieldOptions.createArrayOptions(arrayClass, elementType, packed, elTypeEncode, elFormat);
    }

    public static <C extends Collection<?>> RootFieldOptions<C> createArray(Class<C> arrayClass, Class<?> elementType, boolean packed) {
        return RootFieldOptions.createArrayOptions(arrayClass, elementType, packed, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public static <C extends Collection<?>> RootFieldOptions<C> createRepeat(Class<C> collectionClass, Class<?> elementType, boolean packed,
            TypeEncode elTypeEncode, FieldFormat elFormat) {
        return RootFieldOptions.createRepeatOptions(collectionClass, elementType, packed, elTypeEncode, elFormat);
    }

    public static <C extends Collection<?>> RootFieldOptions<C> createRepeat(Class<C> collectionClass, Class<?> elementType, boolean packed) {
        return RootFieldOptions.createRepeatOptions(collectionClass, elementType, packed, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public static FieldOptions<Map<?, ?>> createMap(
            Class<?> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
            Class<?> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        return RootFieldOptions.createMapOptions(keyType, keyTypeEncode, keyFormat, valueType, valueTypeEncode, valueFormat);
    }

    public static FieldOptions<Map<?, ?>> createMap(
            Class<?> keyType,
            Class<?> valueType) {
        return RootFieldOptions.createMapOptions(keyType, TypeEncode.DEFAULT, FieldFormat.DEFAULT, valueType, TypeEncode.DEFAULT,
                FieldFormat.DEFAULT);
    }

}
