package com.tny.game.protoex;

import com.tny.game.protoex.annotations.TypeEncode;
import com.tny.game.protoex.field.FieldFormat;
import com.tny.game.protoex.field.IOConfiger;
import com.tny.game.protoex.field.RootIOConfiger;

import java.util.Collection;
import java.util.Map;

public class ProtoExIO {

    public static <T> ProtoExSchema<T> getSchema(ProtoExStream outputStream, Class<?> value) {
        return outputStream.getSchemaContext().getSchema(value);
    }

    public static <T> ProtoExSchema<T> getSchema(ProtoExInputStream inputStream, Class<T> clazz, Tag tag) {
        return inputStream.getSchemaContext().getSchema(tag.getProtoExID(), tag.isRaw(), clazz);
    }

    public static <T> IOConfiger<T> createNormal(ProtoExType protoExType, T type) {
        return createNormal(protoExType, type, false, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public static <T> IOConfiger<T> createNormal(ProtoExType protoExType, T type, TypeEncode typeEncode) {
        return createNormal(protoExType, type, false, typeEncode, FieldFormat.DEFAULT);
    }

    public static <T> IOConfiger<T> createNormal(ProtoExType protoExType, T type, FieldFormat format) {
        return createNormal(protoExType, type, false, TypeEncode.DEFAULT, format);
    }

    public static <T> IOConfiger<T> createNormal(ProtoExType protoExType, T type, boolean packed, TypeEncode typeEncode, FieldFormat format) {
        return RootIOConfiger.createNormalConfiger(protoExType, type, packed, typeEncode, format);
    }

    public static <C extends Collection<?>> RootIOConfiger<C> createRepeat(Class<C> collectionClass, Class<?> elementType, boolean packed, TypeEncode elTypeEncode, FieldFormat elFormat) {
        return RootIOConfiger.createRepeatConfiger(collectionClass, elementType, packed, elTypeEncode, elFormat);
    }

    public static <C extends Collection<?>> RootIOConfiger<C> createRepeat(Class<C> collectionClass, Class<?> elementType, boolean packed) {
        return RootIOConfiger.createRepeatConfiger(collectionClass, elementType, packed, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

    public static IOConfiger<Map<?, ?>> createMap(
            Class<?> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
            Class<?> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        return RootIOConfiger.createMapConfiger(keyType, keyTypeEncode, keyFormat, valueType, valueTypeEncode, valueFormat);
    }

    public static IOConfiger<Map<?, ?>> createMap(
            Class<?> keyType,
            Class<?> valueType) {
        return RootIOConfiger.createMapConfiger(keyType, TypeEncode.DEFAULT, FieldFormat.DEFAULT, valueType, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
    }

}
