package com.tny.game.protoex.field.runtime;

import com.tny.game.common.buff.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 运行时类型描述结构
 *
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public class RuntimeProtoExSchema {

    private static final ConcurrentMap<Type, ProtoExSchema<?>> schemaMap = new ConcurrentHashMap<>();
    // private static final Map<DefineType, ConcurrentMap<Integer, Map<Class<?>, ProtoExSchema<?>>>> typpeSchemaMap = new HashMap<>();
    private static final Map<Integer, Map<Object, ProtoExSchema<?>>> rawSchemaMap = new ConcurrentHashMap<>();
    private static final Map<Integer, ProtoExSchema<?>> customSchemaMap = new ConcurrentHashMap<>();

    static {
        putRawSchema(Integer.TYPE, RuntimePrimitiveSchema.INT_SCHEMA, false);
        putRawSchema(Integer.class, RuntimePrimitiveSchema.INT_SCHEMA, true);
        putRawSchema(Long.TYPE, RuntimePrimitiveSchema.LONG_SCHEMA, false);
        putRawSchema(Long.class, RuntimePrimitiveSchema.LONG_SCHEMA, true);
        putRawSchema(Float.TYPE, RuntimePrimitiveSchema.FLOAT_SCHEMA, false);
        putRawSchema(Float.class, RuntimePrimitiveSchema.FLOAT_SCHEMA, true);
        putRawSchema(Double.TYPE, RuntimePrimitiveSchema.DOUBLE_SCHEMA, false);
        putRawSchema(Double.class, RuntimePrimitiveSchema.DOUBLE_SCHEMA, true);
        putRawSchema(Boolean.TYPE, RuntimePrimitiveSchema.BOOLEAN_SCHEMA, false);
        putRawSchema(Boolean.class, RuntimePrimitiveSchema.BOOLEAN_SCHEMA, true);
        putRawSchema(Character.TYPE, RuntimePrimitiveSchema.CHAR_SCHEMA, false);
        putRawSchema(Character.class, RuntimePrimitiveSchema.CHAR_SCHEMA, true);
        putRawSchema(Short.TYPE, RuntimePrimitiveSchema.SHORT_SCHEMA, false);
        putRawSchema(Short.class, RuntimePrimitiveSchema.SHORT_SCHEMA, true);
        putRawSchema(Byte.TYPE, RuntimePrimitiveSchema.BYTE_SCHEMA, false);
        putRawSchema(Byte.class, RuntimePrimitiveSchema.BYTE_SCHEMA, true);
        putRawSchema(String.class, RuntimePrimitiveSchema.STRING_SCHEMA, true);
        putRawSchema(byte[].class, RuntimePrimitiveSchema.BYTES_SCHEMA, true);
        putRawSchema(AtomicInteger.class, RuntimePrimitiveSchema.ATOMIC_INT_SCHEMA, false);
        putRawSchema(AtomicLong.class, RuntimePrimitiveSchema.ATOMIC_LONG_SCHEMA, false);
        putRawSchema(AtomicBoolean.class, RuntimePrimitiveSchema.ATOMIC_BOOLEAN_SCHEMA, false);
        putRawSchema(LinkedByteBuffer.class, RuntimePrimitiveSchema.LINKED_BUFFER_SCHEMA, false);
        putRawSchema(Collection.class, RuntimeCollectionSchema.COLLECTION_SCHEMA, true);
        putRawSchema(List.class, RuntimeCollectionSchema.COLLECTION_SCHEMA, false);
        putRawSchema(Set.class, RuntimeCollectionSchema.COLLECTION_SCHEMA, false);
        putRawSchema(Map.class, RuntimeMapSchema.MAP_SCHEMA, true);
    }

    private static void putTypeSchema(Type type, ProtoExSchema<?> schema) {
        ProtoExSchema<?> last = schemaMap.putIfAbsent(type, schema);
        if (last != null) {
            throw new IllegalArgumentException(
                    format("{} 类 {} 与 {} protoExID 都为 {} 发生冲突 ", type, last.getName(), schema.getName(), schema.getProtoExId()));
        }
    }

    private static void putRawSchema(Type type, ProtoExSchema<?> schema, boolean defSchema) {
        putTypeSchema(type, schema);
        Map<Object, ProtoExSchema<?>> map = rawSchemaMap.computeIfAbsent(schema.getProtoExId(), k -> new CopyOnWriteMap<>());
        map.put(type, schema);
        if (defSchema) {
            ProtoExSchema<?> last = map.putIfAbsent(schema.getProtoExId(), schema);
            if (last != null) {
                throw new IllegalArgumentException(
                        format("{} 类 {} 与 {} protoExID 都为 {} 发生冲突 ", type, last.getName(), schema.getName(), schema.getProtoExId()));
            }
        }
    }

    private static void putCustomSchema(Type type, ProtoExSchema<?> schema) {
        putTypeSchema(type, schema);
        ProtoExSchema<?> last = customSchemaMap.putIfAbsent(schema.getProtoExId(), schema);
        if (last != null) {
            throw new IllegalArgumentException(
                    format("{} 类 {} 与 {} protoExID 都为 {} 发生冲突 ", type, last.getName(), schema.getName(), schema.getProtoExId()));
        }
    }

    private static <T> boolean putInto(Type type, ProtoExSchema<T> schema) {
        if (schema.getProtoExId() >= 0) {
            if (schema.isRaw()) {
                putRawSchema(type, schema, false);
            } else {
                putCustomSchema(type, schema);
            }
        }
        return true;
    }

    public static <T> ProtoExSchema<T> getProtoSchema(int id, boolean raw) {
        if (raw) {
            Map<Object, ProtoExSchema<?>> schemaMap = rawSchemaMap.get(id);
            if (schemaMap == null) {
                return null;
            }
            return (ProtoExSchema<T>)schemaMap.get(id);
        } else {
            return (ProtoExSchema<T>)customSchemaMap.get(id);
        }
    }

    public static <T> ProtoExSchema<T> getProtoSchema(int id, boolean raw, Class<?> type) {
        ProtoExSchema<T> schema;
        if (type != null && type != Object.class) {
            schema = getProtoSchema(type);
            if (schema != null && (id == 0 || (schema.isRaw() == raw && schema.getProtoExId() == id))) {
                return schema;
            }
        }
        if (raw) {
            Map<Object, ProtoExSchema<?>> schemaMap = rawSchemaMap.get(id);
            if (schemaMap == null) {
                return null;
            }
            if (type != null) {
                schema = (ProtoExSchema<T>)schemaMap.get(type);
                if (schema != null) {
                    return schema;
                }
            }
            schema = (ProtoExSchema<T>)schemaMap.get(id);
            // if (schema != null)
            return schema;
        } else {
            return (ProtoExSchema<T>)customSchemaMap.get(id);
        }
    }

    public static <T> ProtoExSchema<T> getProtoSchema(Type type) {
        ProtoExSchema<T> schema = (ProtoExSchema<T>)schemaMap.get(type);
        if (schema == null) {
            if (type instanceof Class) {
                Class<T> clazz = (Class<T>)type;
                if (clazz.isArray()) {
                    schema = (ProtoExSchema<T>)RuntimeArraySchema.ARRAY_SCHEMA;
                    // throw ProtobufExException.unsupportArray(clazz);
                } else if (clazz.isEnum()) {
                    schema = newEnumSchema((Class<Enum<?>>)clazz);
                } else if (Collection.class.isAssignableFrom(clazz)) {
                    return (ProtoExSchema<T>)RuntimeCollectionSchema.COLLECTION_SCHEMA;
                } else if (Map.class.isAssignableFrom(clazz)) {
                    return (ProtoExSchema<T>)RuntimeMapSchema.MAP_SCHEMA;
                } else {
                    if (clazz.getAnnotation(ProtoEx.class) == null) {
                        return null;
                    }
                    RuntimeMessageSchema<T> newSchema = new RuntimeMessageSchema<>(clazz);
                    if (putInto(type, newSchema)) {
                        return initMessageSchema(newSchema, clazz);
                    } else {
                        return getProtoSchema(type);
                    }
                }
            }
            if (schema == null) {
                return null;
            }
            putInto(type, schema);
        }
        return schema;
    }

    private static <T> ProtoExSchema<T> initMessageSchema(RuntimeMessageSchema<T> schema, Class<T> typeClass) {
        if (typeClass.isInterface() || Modifier.isAbstract(typeClass.getModifiers())) {
            throw new RuntimeException("The root object can neither be an abstract " + "class nor interface: \"" + typeClass.getName());
        }
        if (typeClass.getAnnotation(ProtoEx.class) == null) {
            throw new RuntimeException(format("{} @{} is null", typeClass, ProtoEx.class));
        }

        final Map<String, Field> fieldMap = findInstanceFields(typeClass);
        int maxFieldMapping = 0;
        final ArrayList<FieldDesc<?>> fields = new ArrayList<>(fieldMap.size());
        for (Field f : fieldMap.values()) {
            if (f.getAnnotation(Deprecated.class) != null) {
                continue;
            }

            final ProtoExField tag = f.getAnnotation(ProtoExField.class);
            final int fieldMapping;
            if (tag == null) {
                throw new RuntimeException(
                        "When using annotation-based mapping, " + "all fields must be annotated with @" + ProtoExField.class.getSimpleName());
            } else {
                fieldMapping = tag.value();

                if (fieldMapping < 1) {
                    throw new RuntimeException("Invalid field number: " + fieldMapping + " on " + typeClass);
                }

            }
            Class<?> clazz = f.getType();
            // if (f.getType().isArray() && clazz != byte[].class && clazz != Byte[].class)
            //     throw ProtobufExException.unsupportArray(typeClass, f);
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
        LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<>();
        fill(fieldMap, typeClass);
        return fieldMap;
    }

    private static void fill(Map<String, Field> fieldMap, Class<?> typeClass) {
        if (Object.class != typeClass.getSuperclass()) {
            fill(fieldMap, typeClass.getSuperclass());
        }

        for (Field f : typeClass.getDeclaredFields()) {
            int mod = f.getModifiers();
            if (!Modifier.isStatic(mod) && !Modifier.isTransient(mod) && f.getAnnotation(ProtoExField.class) != null) {
                fieldMap.put(f.getName(), f);
            }
        }
    }

    public static void main(String[] args) {
        RuntimeProtoExSchema.getProtoSchema(int.class);
    }

}
