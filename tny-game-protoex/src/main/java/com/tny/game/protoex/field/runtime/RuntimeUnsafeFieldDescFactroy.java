package com.tny.game.protoex.field.runtime;

import com.tny.game.common.reflect.Wraper;
import com.tny.game.common.utils.buff.LinkedByteBuffer;
import com.tny.game.protoex.ProtoExSchema;
import com.tny.game.protoex.ProtoExType;
import com.tny.game.protoex.ProtobufExException;
import com.tny.game.protoex.annotations.Packed;
import com.tny.game.protoex.annotations.ProtoExElement;
import com.tny.game.protoex.annotations.ProtoExEntry;
import com.tny.game.protoex.annotations.TypeEncode;
import com.tny.game.protoex.field.*;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * 运行时非安全访问字段描述
 *
 * @author KGTny
 */
@SuppressWarnings("restriction")
public class RuntimeUnsafeFieldDescFactroy {

    private static final sun.misc.Unsafe UNSAFE = initUnsafe();

    private static sun.misc.Unsafe initUnsafe() {
        try {
            Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (sun.misc.Unsafe) f.get(null);
        } catch (Exception e) {
        }
        return sun.misc.Unsafe.getUnsafe();
    }

    private RuntimeUnsafeFieldDescFactroy() {
    }

    public static class UnsafeFieldDesc<T> extends BaseIOConfiger<T> implements FieldDesc<T> {

        protected long offset;

        protected UnsafeFieldDesc(ProtoExType protoExType, Class<T> type, boolean packed) {
            super(protoExType, type, "root", 0, packed, TypeEncode.DEFAULT, FieldFormat.DEFAULT);
        }

        protected UnsafeFieldDesc(ProtoExType protoExType, Field field) {
            super(protoExType, field);
            this.offset = UNSAFE.objectFieldOffset(field);
        }

        @Override
        public void setValue(Object message, T value) {
            if (value != null) {
                UNSAFE.putObject(message, this.offset, value);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public T getValue(Object message) {
            return (T) UNSAFE.getObject(message, this.offset);
        }

        //		private ProtoExSchema<T> getSchema(T value) {
        //			if (this.schema != null)
        //				return this.schema;
        //			return RuntimeSchema.getProtoSchema(value.getClass());
        //		}

        //
        //		@SuppressWarnings("unchecked")
        //		protected T getValue(Object message) {
        //			return (T) UNSAFE.getObject(message, offset);
        //		}
        //
        //		protected T readValue(ProtobufInputStream inputStream, Tag tag) {
        //			return (T) this.schema.readValue(inputStream, tag);
        //		}
        //
        //		@Override
        //		protected void readFrom(ProtobufInputStream inputStream, Tag tag, Object message) {
        //			T value = readValue(inputStream, tag);
        //			if (value != null) {
        //				setValue(message, value);
        //			}
        //		}
        //
        //		protected void setValue(Object message, T value) {
        //			if (value != null) {
        //				UNSAFE.putObject(message, offset, value);
        //			}
        //		}

    }

    public static abstract class UnsafePrimitiveFieldDesc<T> extends UnsafeFieldDesc<T> {

        protected boolean primitive;

        protected ProtoExSchema<T> schema;

        protected UnsafePrimitiveFieldDesc(ProtoExType protoExType, Field field) {
            super(protoExType, field);
            if (!Wraper.getPrimitive(field.getType()).isPrimitive())
                throw ProtobufExException.fieldNotPrimitive(field);
            this.primitive = field.getType().isPrimitive();
            this.schema = RuntimeProtoExSchema.getProtoSchema(this.type);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T getValue(Object message) {
            if (this.primitive)
                return this.getPrimitive(message);
            else
                return (T) UNSAFE.getObject(message, this.offset);
        }

        @Override
        public void setValue(Object message, T value) {
            if (value != null) {
                if (this.primitive)
                    this.setPrimitive(message, value);
                else
                    UNSAFE.putObject(message, this.offset, value);
            }
        }

        protected abstract T getPrimitive(Object message);

        protected abstract void setPrimitive(Object message, T value);

    }

    public static final FieldDescFactory<Character> CHAR = new FieldDescFactory<Character>() {

        @Override
        public FieldDesc<Character> create(Field field) {
            return new UnsafePrimitiveFieldDesc<Character>(ProtoExType.CHAR, field) {

                @Override
                protected Character getPrimitive(Object message) {
                    return UNSAFE.getChar(message, this.offset);
                }

                @Override
                protected void setPrimitive(Object message, Character value) {
                    UNSAFE.putChar(message, this.offset, value);
                }

            };
        }

    };

    public static final FieldDescFactory<Short> SHORT = new FieldDescFactory<Short>() {

        @Override
        public FieldDesc<Short> create(Field field) {
            return new UnsafePrimitiveFieldDesc<Short>(ProtoExType.SHORT, field) {

                @Override
                protected Short getPrimitive(Object message) {
                    return UNSAFE.getShort(message, this.offset);
                }

                @Override
                protected void setPrimitive(Object message, Short value) {
                    UNSAFE.putShort(message, this.offset, value);
                }

            };
        }

    };

    public static final FieldDescFactory<Byte> BYTE = new FieldDescFactory<Byte>() {

        @Override
        public FieldDesc<Byte> create(Field field) {
            return new UnsafePrimitiveFieldDesc<Byte>(ProtoExType.BYTE, field) {

                @Override
                protected Byte getPrimitive(Object message) {
                    return UNSAFE.getByte(message, this.offset);
                }

                @Override
                protected void setPrimitive(Object message, Byte value) {
                    UNSAFE.putByte(message, this.offset, value);
                }

            };
        }

    };

    public static final FieldDescFactory<Integer> INTEGER = new FieldDescFactory<Integer>() {

        @Override
        public FieldDesc<Integer> create(Field field) {
            return new UnsafePrimitiveFieldDesc<Integer>(ProtoExType.INT, field) {

                @Override
                protected Integer getPrimitive(Object message) {
                    return UNSAFE.getInt(message, this.offset);
                }

                @Override
                protected void setPrimitive(Object message, Integer value) {
                    UNSAFE.putInt(message, this.offset, value);
                }
            };
        }

    };

    public static final FieldDescFactory<Long> LONG = new FieldDescFactory<Long>() {

        @Override
        public FieldDesc<Long> create(Field field) {
            return new UnsafePrimitiveFieldDesc<Long>(ProtoExType.LONG, field) {

                @Override
                protected Long getPrimitive(Object message) {
                    return UNSAFE.getLong(message, this.offset);
                }

                @Override
                protected void setPrimitive(Object message, Long value) {
                    UNSAFE.putLong(message, this.offset, value);
                }
            };
        }

    };

    public static final FieldDescFactory<Float> FLOAT = new FieldDescFactory<Float>() {

        @Override
        public FieldDesc<Float> create(Field field) {
            return new UnsafePrimitiveFieldDesc<Float>(ProtoExType.FLOAT, field) {

                @Override
                protected Float getPrimitive(Object message) {
                    return UNSAFE.getFloat(message, this.offset);
                }

                @Override
                protected void setPrimitive(Object message, Float value) {
                    UNSAFE.putFloat(message, this.offset, value);
                }
            };
        }

    };

    public static final FieldDescFactory<Double> DOUBLE = new FieldDescFactory<Double>() {

        @Override
        public FieldDesc<Double> create(Field field) {

            return new UnsafePrimitiveFieldDesc<Double>(ProtoExType.DOUBLE, field) {

                @Override
                protected Double getPrimitive(Object message) {
                    return UNSAFE.getDouble(message, this.offset);
                }

                @Override
                protected void setPrimitive(Object message, Double value) {
                    UNSAFE.putDouble(message, this.offset, value);
                }
            };
        }

    };

    public static final FieldDescFactory<Boolean> BOOLEAN = new FieldDescFactory<Boolean>() {

        @Override
        public FieldDesc<Boolean> create(Field field) {
            return new UnsafePrimitiveFieldDesc<Boolean>(ProtoExType.BOOLEAN, field) {

                @Override
                protected Boolean getPrimitive(Object message) {
                    return UNSAFE.getBoolean(message, this.offset);
                }

                @Override
                protected void setPrimitive(Object message, Boolean value) {
                    UNSAFE.putBoolean(message, this.offset, value);
                }

            };
        }

    };

    public static final FieldDescFactory<String> STRING = new FieldDescFactory<String>() {

        @Override
        public FieldDesc<String> create(Field field) {
            return new UnsafeFieldDesc<String>(ProtoExType.STRING, field);
        }

    };

    public static final FieldDescFactory<byte[]> BYTE_ARRAY = new FieldDescFactory<byte[]>() {

        @Override
        public FieldDesc<byte[]> create(Field field) {
            return new UnsafeFieldDesc<byte[]>(ProtoExType.BYTES, field);
        }

    };

    public static final FieldDescFactory<LinkedByteBuffer> LINKED_BYTE_BUFFER = new FieldDescFactory<LinkedByteBuffer>() {

        @Override
        public FieldDesc<LinkedByteBuffer> create(Field field) {
            return new UnsafeFieldDesc<LinkedByteBuffer>(ProtoExType.BYTES, field);
        }

    };

    public static final FieldDescFactory<Enum<?>> ENUM = new FieldDescFactory<Enum<?>>() {

        @Override
        public FieldDesc<Enum<?>> create(Field field) {
            return new UnsafeFieldDesc<Enum<?>>(ProtoExType.ENUM, field);
        }

    };

    public static final FieldDescFactory<Object> OBJECT = new FieldDescFactory<Object>() {

        @Override
        public FieldDesc<Object> create(Field field) {
            return new UnsafeFieldDesc<Object>(ProtoExType.MESSAGE, field);
        }

    };

    public static class UnsafeRepeatFieldDesc extends UnsafeFieldDesc<Object> implements RepeatIOConfiger<Object> {

        private IOConfiger<?> elementDesc;

        @SuppressWarnings("unchecked")
        protected UnsafeRepeatFieldDesc(Field field) {
            super(ProtoExType.REPEAT, field);
            Class<Object> elementType = (Class<Object>) getComponentType(field);
            ProtoExElement protoExElement = field.getAnnotation(ProtoExElement.class);
            if (protoExElement == null) {
                //				throw new NullPointerException(LogUtils.format("{} 类 {} 字段不能存在 @{}", field.getDeclaringClass(), field, ProtoExElement.class));
                this.elementDesc = new SimpleIOConfiger<Object>(elementType, 0, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
            } else {
                this.elementDesc = new SimpleIOConfiger<Object>(elementType, 0, protoExElement.value());
            }
            Packed packed = field.getAnnotation(Packed.class);
            ProtoExType elementExType = ProtoExType.getProtoExType(elementType);
            if (elementExType.isRaw()) {
                this.packed = true;
            } else if (Modifier.isAbstract(elementType.getModifiers()) || elementType == Object.class) {
                this.packed = false;
            } else if (packed != null) {
                this.packed = packed.value();
            } else {
                this.packed = true;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setValue(Object message, Object value) {
            if (value == null)
                return;
            Collection<Object> collection = (Collection<Object>) this.getValue(message);
            if (collection == null) {
                collection = CollectionCreator.createCollection(this.type);
                super.setValue(message, collection);
            }
            if (value instanceof Collection) {
                collection.addAll((Collection<?>) value);
            } else {
                collection.add(value);
            }
        }

        @Override
        public IOConfiger<?> getElementConfiger() {
            return this.elementDesc;
        }

    }

    public static class UnsafeMapFieldDesc extends UnsafeFieldDesc<Object> implements MapIOConfiger<Object> {

        private IOConfiger<?> keyDesc;
        private IOConfiger<?> valueDesc;

        @SuppressWarnings("unchecked")
        protected UnsafeMapFieldDesc(Field field) {
            super(ProtoExType.MAP, field);
            Type[] paramType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
            Class<Object> keyClass = (Class<Object>) paramType[0];
            Class<Object> valueClass = (Class<Object>) paramType[1];
            ProtoExEntry protoExEntry = field.getAnnotation(ProtoExEntry.class);
            if (protoExEntry == null) {
                //				throw new NullPointerException(LogUtils.format("{} 类 {} 字段不能存在 @{}", field.getDeclaringClass(), field, ProtoExEntry.class));
                this.keyDesc = new SimpleIOConfiger<Object>(EntryType.KEY, keyClass, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
                this.valueDesc = new SimpleIOConfiger<Object>(EntryType.VALUE, valueClass, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
            } else {
                this.keyDesc = new SimpleIOConfiger<Object>(EntryType.KEY, keyClass, protoExEntry.key());
                this.valueDesc = new SimpleIOConfiger<Object>(EntryType.VALUE, valueClass, protoExEntry.value());
            }
        }

        @Override
        public IOConfiger<?> getKeyConfiger() {
            return this.keyDesc;
        }

        @Override
        public IOConfiger<?> getValueConfiger() {
            return this.valueDesc;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setValue(Object message, Object value) {
            if (value == null)
                return;
            Map<Object, Object> map = (Map<Object, Object>) this.getValue(message);
            if (map == null) {
                map = CollectionCreator.createMap(this.type);
                super.setValue(message, map);
            }
            if (value instanceof Map) {
                map.putAll((Map<?, ?>) value);
            }
        }

    }

    public static final FieldDescFactory<Object> COLLECTION = new FieldDescFactory<Object>() {

        @Override
        public FieldDesc<Object> create(Field field) {
            return new UnsafeRepeatFieldDesc(field);
        }

    };

    public static final FieldDescFactory<Object> MAP = new FieldDescFactory<Object>() {

        @Override
        public FieldDesc<Object> create(Field field) {
            return new UnsafeMapFieldDesc(field);
        }

    };

    public static Class<?> getComponentType(Field field) {
        Class<?> typeClass = field.getType();
        if (typeClass.isArray()) {
            return field.getType().getComponentType();
        } else if (Collection.class.isAssignableFrom(typeClass)) {
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }
        return typeClass;
    }

    static class Test {

        Integer[] data;

    }

    public static void main(String[] args) {
        Test test = new Test();
        test.data = (Integer[]) Array.newInstance(Integer.class, 3);
        for (int i = 0; i < test.data.length; i++) {
            test.data[i] = i + 1;
        }
        System.out.println(Arrays.toString(test.data));
    }

}