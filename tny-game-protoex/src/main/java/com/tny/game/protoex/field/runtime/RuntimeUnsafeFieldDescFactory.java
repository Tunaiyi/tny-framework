/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex.field.runtime;

import com.tny.game.common.buff.LinkedBuffer;
import com.tny.game.common.type.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.exception.*;
import com.tny.game.protoex.field.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * 运行时非安全访问字段描述
 *
 * @author KGTny
 */
@SuppressWarnings("restriction")
public class RuntimeUnsafeFieldDescFactory {

    private static final sun.misc.Unsafe UNSAFE = initUnsafe();

    private static sun.misc.Unsafe initUnsafe() {
        try {
            Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (sun.misc.Unsafe)f.get(null);
        } catch (Exception e) {
            throw new UnsafeInitException(e);
        }
    }

    private RuntimeUnsafeFieldDescFactory() {
    }

    public static class UnsafeFieldDesc<T> extends BaseFieldOptions<T> implements FieldDesc<T> {

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
            return (T)UNSAFE.getObject(message, this.offset);
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
            if (!Wrapper.getPrimitive(field.getType()).isPrimitive()) {
                throw ProtobufExException.fieldNotPrimitive(field);
            }
            this.primitive = field.getType().isPrimitive();
            this.schema = RuntimeProtoExSchema.getProtoSchema(this.type);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T getValue(Object message) {
            if (this.primitive) {
                return this.getPrimitive(message);
            } else {
                return (T)UNSAFE.getObject(message, this.offset);
            }
        }

        @Override
        public void setValue(Object message, T value) {
            if (value != null) {
                if (this.primitive) {
                    this.setPrimitive(message, value);
                } else {
                    UNSAFE.putObject(message, this.offset, value);
                }
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

    public static final FieldDescFactory<String> STRING = field -> new UnsafeFieldDesc<>(ProtoExType.STRING, field);

    public static final FieldDescFactory<byte[]> BYTE_ARRAY = field -> new UnsafeFieldDesc<>(ProtoExType.BYTES, field);

    public static final FieldDescFactory<LinkedBuffer> LINKED_BYTE_BUFFER = field -> new UnsafeFieldDesc<>(ProtoExType.BYTES, field);

    public static final FieldDescFactory<Enum<?>> ENUM = field -> new UnsafeFieldDesc<>(ProtoExType.ENUM, field);

    public static final FieldDescFactory<Object> OBJECT = field -> new UnsafeMessageFieldDesc(field);

    public static class UnsafeMessageFieldDesc extends UnsafeFieldDesc<Object> implements RepeatFieldOptions<Object> {

        private FieldOptions<?> elementDesc;

        protected UnsafeMessageFieldDesc(Field field) {
            super(ProtoExType.MESSAGE, field);
            ProtoExElement protoExElement = field.getAnnotation(ProtoExElement.class);
            if (protoExElement == null) {
                //				throw new NullPointerException(LogUtils.format("{} 类 {} 字段不能存在 @{}", field.getDeclaringClass(), field,
                //				ProtoExElement.class));
                this.elementDesc = new SimpleFieldOptions<>(Object.class, 0, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
            } else {
                this.elementDesc = new SimpleFieldOptions<>(Object.class, 0, protoExElement.value());
            }
            this.packed = false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setValue(Object message, Object value) {
            if (value == null) {
                return;
            }
            super.setValue(message, value);
        }

        @Override
        public FieldOptions<?> getElementOptions() {
            return this.elementDesc;
        }

    }

    public static class UnsafeRepeatFieldDesc extends UnsafeFieldDesc<Object> implements RepeatFieldOptions<Object> {

        private FieldOptions<?> elementDesc;

        @SuppressWarnings("unchecked")
        protected UnsafeRepeatFieldDesc(Field field) {
            super(ProtoExType.REPEAT, field);
            Class<Object> elementType = (Class<Object>)getComponentType(field);
            ProtoExElement protoExElement = field.getAnnotation(ProtoExElement.class);
            if (protoExElement == null) {
                //				throw new NullPointerException(LogUtils.format("{} 类 {} 字段不能存在 @{}", field.getDeclaringClass(), field,
                //				ProtoExElement.class));
                this.elementDesc = new SimpleFieldOptions<>(elementType, 0, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
            } else {
                this.elementDesc = new SimpleFieldOptions<>(elementType, 0, protoExElement.value());
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
            if (value == null) {
                return;
            }
            super.setValue(message, value);
            // Collection<Object> collection = (Collection<Object>) this.getValue(message);
            // if (collection == null) {
            //     collection = CollectionCreator.createCollection(this.type);
            //     super.setValue(message, collection);
            // }
            // if (value instanceof Collection) {
            //     collection.addAll((Collection<?>) value);
            // } else {
            //     collection.add(value);
            // }
        }

        @Override
        public FieldOptions<?> getElementOptions() {
            return this.elementDesc;
        }

    }

    public static class UnsafeMapFieldDesc extends UnsafeFieldDesc<Object> implements MapFieldOptions<Object> {

        private FieldOptions<?> keyDesc;

        private FieldOptions<?> valueDesc;

        @SuppressWarnings("unchecked")
        protected UnsafeMapFieldDesc(Field field) {
            super(ProtoExType.MAP, field);
            Type[] paramType = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
            Class<Object> keyClass = (Class<Object>)paramType[0];
            Class<Object> valueClass = (Class<Object>)paramType[1];
            ProtoExEntry protoExEntry = field.getAnnotation(ProtoExEntry.class);
            if (protoExEntry == null) {
                //				throw new NullPointerException(LogUtils.format("{} 类 {} 字段不能存在 @{}", field.getDeclaringClass(), field, ProtoExEntry
                //				.class));
                this.keyDesc = new SimpleFieldOptions<>(EntryType.KEY, keyClass, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
                this.valueDesc = new SimpleFieldOptions<>(EntryType.VALUE, valueClass, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
            } else {
                this.keyDesc = new SimpleFieldOptions<>(EntryType.KEY, keyClass, protoExEntry.key());
                this.valueDesc = new SimpleFieldOptions<>(EntryType.VALUE, valueClass, protoExEntry.value());
            }
        }

        @Override
        public FieldOptions<?> getKeyOptions() {
            return this.keyDesc;
        }

        @Override
        public FieldOptions<?> getValueOptions() {
            return this.valueDesc;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setValue(Object message, Object value) {
            if (value == null) {
                return;
            }
            Map<Object, Object> map = (Map<Object, Object>)this.getValue(message);
            if (map == null) {
                map = CollectionCreator.createMap(this.type);
                super.setValue(message, map);
            }
            if (value instanceof Map) {
                map.putAll((Map<?, ?>)value);
            }
        }

    }

    public static final FieldDescFactory<Object> REPEAT = field -> new UnsafeRepeatFieldDesc(field);

    public static final FieldDescFactory<Object> MAP = field -> new UnsafeMapFieldDesc(field);

    public static Class<?> getComponentType(Field field) {
        Class<?> typeClass = field.getType();
        if (typeClass.isArray()) {
            return field.getType().getComponentType();
        } else if (Collection.class.isAssignableFrom(typeClass)) {
            return (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
        }
        return typeClass;
    }

    static class Test {

        Integer[] data;

    }

    public static void main(String[] args) {
        Test test = new Test();
        test.data = (Integer[])Array.newInstance(Integer.class, 3);
        for (int i = 0; i < test.data.length; i++) {
            test.data[i] = i + 1;
        }
        System.out.println(Arrays.toString(test.data));
    }

}
