package com.tny.game.common.utils;

import com.tny.game.common.enums.*;
import com.tny.game.common.reflect.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 16/3/9.
 */
public class ObjectAide extends ObjectUtils {

    public static <T> T none() {
        return null;
    }

    public static <T> T none(Class<T> clazz) {
        return null;
    }

    public static <T> T ifNull(T object, T defObject) {
        return object == null ? defObject : object;
    }

    public static <T> T ifNullAndGet(T object, Supplier<T> defObject) {
        return object == null ? defObject.get() : object;
    }

    public static <T, O> O ifNotNull(T object, Function<T, O> mapper, O defObject) {
        return object == null ? defObject : mapper.apply(object);
    }

    private static <T, O> O ifNotNullElse(T object, Function<T, O> mapper, Supplier<? extends O> supplier) {
        return object == null ? supplier.get() : mapper.apply(object);
    }

    public static <V, R> R test(V value, Predicate<V> condition, R trueValue, R falseValue) {
        if (condition.test(value))
            return trueValue;
        return falseValue;
    }

    public static <V> V test(V value, Predicate<V> condition, V falseValue) {
        if (condition.test(value))
            return value;
        return falseValue;
    }

    public static <V, R> R testAndApply(V value, Predicate<V> condition, Function<V, R> trueValue, Function<V, R> falseValue) {
        if (condition.test(value))
            return trueValue.apply(value);
        return falseValue.apply(value);
    }

    public static <V, R> R ifEquals(V one, V other, R trueValue, R falseValue) {
        if (Objects.equals(one, other))
            return trueValue;
        return falseValue;
    }

    public static <V, R> R ifEqualsAndGet(V one, V other, Supplier<R> trueValue, Supplier<R> falseValue) {
        if (Objects.equals(one, other))
            return trueValue.get();
        return falseValue.get();
    }

    public static <T> T self(T object) {
        return object;
    }

    @SuppressWarnings("unchecked")
    public static <T> T as(Object object) {
        return (T) object;
    }

    @SuppressWarnings("unchecked")
    public static <T> T as(Object object, Class<T> clazz) {
        if (object == null)
            return null;
        if (clazz.isInstance(object))
            return (T) object;
        else if (clazz == int.class || clazz == Integer.class) {
            if (object instanceof Integer) {
                return as(object);
            } else if (object instanceof Number) {
                return as(as(object, Number.class).intValue());
            } else if (object instanceof String) {
                return as(NumberUtils.toInt(as(object)));
            }
        } else if (clazz == long.class || clazz == Long.class) {
            if (object instanceof Long) {
                return as(object);
            } else if (object instanceof Number) {
                return as(as(object, Number.class).longValue());
            } else if (object instanceof String) {
                return as(NumberUtils.toLong(as(object)));
            }
        } else if (clazz == float.class || clazz == Float.class) {
            if (object instanceof Float) {
                return as(object);
            } else if (object instanceof Number) {
                return as(as(object, Number.class).floatValue());
            } else if (object instanceof String) {
                return as(NumberUtils.toFloat(as(object)));
            }
        } else if (clazz == double.class || clazz == Double.class) {
            if (object instanceof Double) {
                return as(object);
            } else if (object instanceof Number) {
                return as(as(object, Number.class).doubleValue());
            } else if (object instanceof String) {
                return as(NumberUtils.toDouble(as(object, String.class)));
            }
        } else if (clazz == short.class || clazz == Short.class) {
            if (object instanceof Short) {
                return as(object);
            } else if (object instanceof Number) {
                return as(as(object, Number.class).shortValue());
            } else if (object instanceof String) {
                return as(NumberUtils.toShort(as(object)));
            }
        } else if (clazz == byte.class || clazz == Byte.class) {
            if (object instanceof Byte) {
                return as(object);
            } else if (object instanceof Number) {
                return as(as(object, Number.class).byteValue());
            } else if (object instanceof String) {
                return as(NumberUtils.toByte(as(object)));
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            if (object instanceof Boolean)
                return as(object);
            else if (object instanceof Number) {
                return as(as(object, Number.class).longValue() > 0);
            } else if (object instanceof String) {
                return as(Boolean.valueOf(object.toString().toLowerCase()));
            }
        } else if (Enum.class.isAssignableFrom(clazz)) {
            T value = null;
            if (object instanceof String)
                value = EnumAide.uncheckOfName(clazz, as(object));
            if (value == null && EnumIdentifiable.class.isAssignableFrom(clazz))
                value = EnumAide.uncheckOf(as(clazz), object);
            return value;
        }
        throw new ClassCastException(object + "is not " + clazz + "instance");
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClassType(ReferenceType<T> referenceType) {
        Type[] types = referenceType.getClass().getGenericInterfaces();
        Type subType = ((ParameterizedType) types[0]).getActualTypeArguments()[0];
        Class<T> clazz = null;
        if (subType instanceof Class) {
            clazz = (Class<T>) subType;
        } else if (subType instanceof ParameterizedType) {
            clazz = (Class<T>) ((ParameterizedType) subType).getRawType();
        }
        return clazz;
    }

    public static <T> T converTo(Object object, ReferenceType<T> referenceType) {
        return converTo(object, getClassType(referenceType));
    }


    public static <T> T converTo(Object object, Class<T> clazz) {
        if (object == null)
            return null;
        final Class<?> checkClass = !clazz.isPrimitive() ? clazz : Wraper.getWraper(clazz);
        if (checkClass.isInstance(object)) {
            return as(object);
        }
        if (Enum.class.isAssignableFrom(checkClass) && object instanceof String) {
            Class<? extends Enum> enumClass = as(checkClass);
            @SuppressWarnings("unchecked")
            Object enumObject = Enum.valueOf(enumClass, object.toString());
            return as(enumObject);
        }
        if (checkClass == Long.class) {
            Number number = Double.parseDouble(object.toString());
            object = number.longValue();
            return as(object);
        }
        if (object instanceof Number) {
            Number number = (Number) object;
            if (checkClass == Integer.class) {
                return as(number.intValue());
            }
            if (checkClass == Byte.class) {
                return as(number.byteValue());
            }
            if (checkClass == Float.class) {
                return as(number.floatValue());
            }
            if (checkClass == Short.class) {
                return as(number.shortValue());
            }
            if (EnumIdentifiable.class.isAssignableFrom(clazz) && Enum.class.isAssignableFrom(clazz)) {
                for (T value : clazz.getEnumConstants()) {
                    Object id = ((EnumIdentifiable) value).getId();
                    if (id instanceof Number) {
                        if (((Number) id).intValue() == ((Number) object).intValue())
                            return value;
                    } else {
                        throw new IllegalArgumentException(format("can not find Enum {} where id is {}", clazz, object));
                    }
                }
            }
        }
        if (!clazz.isAssignableFrom(object.getClass()))
            throw new ClassCastException(format("{} can not conver to {}", object.getClass(), clazz));
        return as(object);
    }

}
