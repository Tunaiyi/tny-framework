package com.tny.game.common.utils;

import com.tny.game.common.enums.*;
import com.tny.game.common.number.*;
import com.tny.game.common.type.*;

import java.lang.reflect.*;
import java.util.Objects;
import java.util.function.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 16/3/9.
 */
public class ObjectAide {

	public static void noOps() {
	}

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
		if (condition.test(value)) {
			return trueValue;
		}
		return falseValue;
	}

	public static <V> V test(V value, Predicate<V> condition, V falseValue) {
		if (condition.test(value)) {
			return value;
		}
		return falseValue;
	}

	public static <V, R> R testAndApply(V value, Predicate<V> condition, Function<V, R> trueValue, Function<V, R> falseValue) {
		if (condition.test(value)) {
			return trueValue.apply(value);
		}
		return falseValue.apply(value);
	}

	public static <V, R> R ifEquals(V one, V other, R trueValue, R falseValue) {
		if (Objects.equals(one, other)) {
			return trueValue;
		}
		return falseValue;
	}

	public static <V, R> R ifEqualsAndGet(V one, V other, Supplier<R> trueValue, Supplier<R> falseValue) {
		if (Objects.equals(one, other)) {
			return trueValue.get();
		}
		return falseValue.get();
	}

	public static <T> T self(T object) {
		return object;
	}

	@SuppressWarnings("unchecked")
	public static <T> T as(Object object) {
		return (T)object;
	}

	@SuppressWarnings("unchecked")
	public static <T> T as(Object object, Class<T> clazz) {
		if (object == null) {
			return null;
		}
		if (clazz.isInstance(object)) {
			return (T)object;
		}
		throw new ClassCastException(object + "is not " + clazz + "instance");
	}

	@SuppressWarnings("unchecked")
	public static <T> T as(Object object, ReferenceType<T> referenceType) {
		if (object == null) {
			return null;
		}
		return (T)object;
		//		throw new ClassCastException(object + "is not " + clazz + "instance");
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> getClassType(ReferenceType<T> referenceType) {
		Type[] types = referenceType.getClass().getGenericInterfaces();
		Type subType = ((ParameterizedType)types[0]).getActualTypeArguments()[0];
		Class<T> clazz = null;
		if (subType instanceof Class) {
			clazz = (Class<T>)subType;
		} else if (subType instanceof ParameterizedType) {
			clazz = (Class<T>)((ParameterizedType)subType).getRawType();
		}
		return clazz;
	}

	private static <E extends Enum<?>> E enumConvert(Object source, Class<E> clazz) {
		E value = null;
		if (source instanceof String) {
			value = EnumAide.ofName(clazz, as(source));
		}
		if (value == null && Enumerable.class.isAssignableFrom(clazz)) {
			value = as(EnumAide.of(as(clazz), source));
		}
		return value;
	}

	public static <T> T convertTo(Object object, ReferenceType<T> referenceType) {
		return convertTo(object, getClassType(referenceType));
	}

	public static <T> T convertTo(Object source, Class<T> clazz) {
		if (source == null) {
			return null;
		}
		if (clazz.isInstance(source)) {
			return as(source);
		}
		final Class<?> targetClass = !clazz.isPrimitive() ? clazz : Wrapper.getWrapper(clazz);
		if (targetClass.isInstance(source)) {
			return as(source);
		}
		if (Enum.class.isAssignableFrom(targetClass)) {
			@SuppressWarnings("rawtypes")
			Class<? extends Enum> enumClass = as(targetClass);
			return as(enumConvert(source, enumClass));
		}
		if (Number.class.isAssignableFrom(targetClass)) {
			Class<? extends Number> numberClass = as(targetClass);
			if (source instanceof String) {
				return as(NumberAide.parse(as(source), numberClass));
			}
			if (source instanceof Number) {
				Number number = as(source);
				return as(NumberAide.as(number, numberClass));
			}
		}
		if (targetClass == String.class) {
			return as(source.toString());
		}
		if (!clazz.isAssignableFrom(source.getClass())) {
			throw new ClassCastException(format("{} can not conver to {}", source.getClass(), clazz));
		}
		return as(source);
	}

}
