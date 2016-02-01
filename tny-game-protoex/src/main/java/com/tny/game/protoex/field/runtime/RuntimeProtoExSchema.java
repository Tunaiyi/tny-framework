package com.tny.game.protoex.field.runtime;

import com.tny.game.LogUtils;
import com.tny.game.common.utils.buff.LinkedByteBuffer;
import com.tny.game.protoex.ProtoExSchema;
import com.tny.game.protoex.ProtobufExException;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.protoex.field.DefineType;
import com.tny.game.protoex.field.FieldDesc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 运行时类型描述结构
 *
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public class RuntimeProtoExSchema {

    private static final ConcurrentMap<Type, ProtoExSchema<?>> schemaMap = new ConcurrentHashMap<Type, ProtoExSchema<?>>();
    private static final Map<DefineType, ConcurrentMap<Integer, ProtoExSchema<?>>> typpeSchemaMap = new HashMap<DefineType, ConcurrentMap<Integer, ProtoExSchema<?>>>();

    static {

        schemaMap.put(Integer.TYPE, RuntimePrimitiveSchema.INT_SCHEMA);
        schemaMap.put(Integer.class, RuntimePrimitiveSchema.INT_SCHEMA);
        schemaMap.put(Long.TYPE, RuntimePrimitiveSchema.LONG_SCHEMA);
        schemaMap.put(Long.class, RuntimePrimitiveSchema.LONG_SCHEMA);
        schemaMap.put(Float.TYPE, RuntimePrimitiveSchema.FLOAT_SCHEMA);
        schemaMap.put(Float.class, RuntimePrimitiveSchema.FLOAT_SCHEMA);
        schemaMap.put(Double.TYPE, RuntimePrimitiveSchema.DOUBLE_SCHEMA);
        schemaMap.put(Double.class, RuntimePrimitiveSchema.DOUBLE_SCHEMA);
        schemaMap.put(Boolean.TYPE, RuntimePrimitiveSchema.BOOLEAN_SCHEMA);
        schemaMap.put(Boolean.class, RuntimePrimitiveSchema.BOOLEAN_SCHEMA);
        schemaMap.put(Character.TYPE, RuntimePrimitiveSchema.CHAR_SCHEMA);
        schemaMap.put(Character.class, RuntimePrimitiveSchema.CHAR_SCHEMA);
        schemaMap.put(Short.TYPE, RuntimePrimitiveSchema.SHORT_SCHEMA);
        schemaMap.put(Short.class, RuntimePrimitiveSchema.SHORT_SCHEMA);
        schemaMap.put(Byte.TYPE, RuntimePrimitiveSchema.BYTE_SCHEMA);
        schemaMap.put(Byte.class, RuntimePrimitiveSchema.BYTE_SCHEMA);
        schemaMap.put(String.class, RuntimePrimitiveSchema.STRING_SCHEMA);
        schemaMap.put(byte[].class, RuntimePrimitiveSchema.BYTES_SCHEMA);
        schemaMap.put(LinkedByteBuffer.class, RuntimePrimitiveSchema.LINKED_BUFFER_SCHEMA);
        for (DefineType type : DefineType.values()) {
            typpeSchemaMap.put(type, new ConcurrentHashMap<Integer, ProtoExSchema<?>>());
        }
        for (ProtoExSchema<?> schema : schemaMap.values()) {
            typpeSchemaMap.get(DefineType.RAW).putIfAbsent(schema.getProtoExID(), schema);
        }
    }

    public static <T> ProtoExSchema<T> getProtoSchema(int id, boolean raw) {
        return (ProtoExSchema<T>) typpeSchemaMap.get(DefineType.get(raw)).get(id);
    }

    public static void main(String[] args) {
        RuntimeProtoExSchema.getProtoSchema(int.class);
    }

    public static <T> ProtoExSchema<T> getProtoSchema(Class<?> type) {
        ProtoExSchema<T> schema = (ProtoExSchema<T>) schemaMap.get(type);
        if (schema == null) {
            if (type instanceof Class) {
                Class<T> clazz = (Class<T>) type;
                if (clazz.isArray()) {
                    throw ProtobufExException.unsupportArray(clazz);
                } else if (clazz.isEnum()) {
                    schema = newEnumSchema((Class<Enum<?>>) clazz);
                } else if (Collection.class.isAssignableFrom(clazz)) {
                    return (ProtoExSchema<T>) RuntimeRepeatSchema.REPEAT_SCHEMA;
                } else if (Map.class.isAssignableFrom(clazz)) {
                    return (ProtoExSchema<T>) RuntimeMapSchema.MAP_SCHEMA;
                } else {
                    if (clazz.getAnnotation(ProtoEx.class) == null)
                        return null;
                    RuntimeMessageSchema<T> newSchema = new RuntimeMessageSchema<T>(clazz);
                    if (putInto(type, newSchema)) {
                        return initMessageSchema(newSchema, clazz);
                    } else {
                        return getProtoSchema(type);
                    }
                }
            }
            //			else if (type instanceof ParameterizedType) {
            //				ParameterizedType paramType = (ParameterizedType) type;
            //				Class<?> rawClass = (Class<?>) paramType.getRawType();
            //				if (Collection.class.isAssignableFrom(rawClass)) {
            //					schema = (ProtoExSchema<T>) RuntimeRepeatSchema.REPEAT_SCHEMA;
            //				} else if (Map.class.isAssignableFrom(rawClass)) {
            //					schema = (ProtoExSchema<T>) RuntimeMapSchema.MAP_SCHEMA;
            //				}
            //			}
            if (schema == null)
                return null;
            //				throw new IllegalArgumentException(LogUtils.format("{} 无发创建对应的 schema", type));
            putInto(type, schema);
        }
        return schema;
    }

    private static <T> boolean putInto(Type type, ProtoExSchema<T> schema) {
        ProtoExSchema<T> old = (ProtoExSchema<T>) schemaMap.putIfAbsent(type, schema);
        if (old != null) {
            return false;
        } else {
            if (schema.getProtoExID() >= 0) {
                ConcurrentMap<Integer, ProtoExSchema<?>> idSchemaMap = typpeSchemaMap.get(DefineType.get(schema.isRaw()));
                ProtoExSchema<?> last = idSchemaMap.putIfAbsent(schema.getProtoExID(), schema);
                if (last != null)
                    throw new IllegalArgumentException(LogUtils.format("{} 类 {} 与 {} protoExID 都为 {} 发生冲突 ", type, last.getName(), schema.getName(), schema.getProtoExID()));
            }
            return true;
        }
    }

    private static <T> ProtoExSchema<T> initMessageSchema(RuntimeMessageSchema<T> schema, Class<T> typeClass) {
        if (typeClass.isInterface() || Modifier.isAbstract(typeClass.getModifiers())) {
            throw new RuntimeException("The root object can neither be an abstract " + "class nor interface: \"" + typeClass.getName());
        }
        if (typeClass.getAnnotation(ProtoEx.class) == null)
            throw new RuntimeException(LogUtils.format("{} @{} is null", typeClass, ProtoEx.class));

        final Map<String, Field> fieldMap = findInstanceFields(typeClass);
        int maxFieldMapping = 0;
        final ArrayList<FieldDesc<?>> fields = new ArrayList<FieldDesc<?>>(fieldMap.size());
        for (Field f : fieldMap.values()) {
            if (f.getAnnotation(Deprecated.class) != null) {
                continue;
            }

            final ProtoExField tag = f.getAnnotation(ProtoExField.class);
            final int fieldMapping;
            if (tag == null) {
                throw new RuntimeException("When using annotation-based mapping, " + "all fields must be annotated with @" + ProtoExField.class.getSimpleName());
            } else {
                fieldMapping = tag.value();

                if (fieldMapping < 1) {
                    throw new RuntimeException("Invalid field number: " + fieldMapping + " on " + typeClass);
                }

            }
            Class<?> clazz = f.getType();
            if (f.getType().isArray() && clazz != byte[].class && clazz != Byte[].class)
                throw ProtobufExException.unsupportArray(typeClass, f);
            final FieldDesc<?> field = RuntimeFieldDescFactory.getFactory(clazz).create(f);
            fields.add(field);
            maxFieldMapping = Math.max(maxFieldMapping, fieldMapping);
        }
        if (fields.isEmpty()) {
            throw new RuntimeException("Not able to map any fields from " + typeClass + ".  All fields are either transient/static.");
        }
        schema.init(typeClass, fields.toArray(new FieldDesc<?>[0]), maxFieldMapping);
        return schema;
    }

    /**
     * 注册对应的类型描述结构
     *
     * @param typeClass
     * @param schema
     * @return
     */
    public static <T> boolean register(Class<T> typeClass, ProtoExSchema<T> schema) {
        return schemaMap.putIfAbsent(typeClass, schema) != null;
    }

    /**
     * 移除对应的类型描述结构
     *
     * @param typeClass
     * @param schema
     * @return
     */
    public static <T> boolean remove(Class<T> typeClass, ProtoExSchema<T> schema) {
        return schemaMap.remove(typeClass, schema);
    }

    @SuppressWarnings("rawtypes")
    private static <T> ProtoExSchema<T> newEnumSchema(Class<Enum<?>> type) {
        return new EnumSchema(type);
    }

    private static Map<String, Field> findInstanceFields(Class<?> typeClass) {
        LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<String, Field>();
        fill(fieldMap, typeClass);
        return fieldMap;
    }

    private static void fill(Map<String, Field> fieldMap, Class<?> typeClass) {
        if (Object.class != typeClass.getSuperclass())
            fill(fieldMap, typeClass.getSuperclass());

        for (Field f : typeClass.getDeclaredFields()) {
            int mod = f.getModifiers();
            if (!Modifier.isStatic(mod) && !Modifier.isTransient(mod) && f.getAnnotation(ProtoExField.class) != null)
                fieldMap.put(f.getName(), f);
        }
    }

}
