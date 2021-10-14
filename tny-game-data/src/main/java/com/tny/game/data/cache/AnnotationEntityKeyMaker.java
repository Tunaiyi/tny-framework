package com.tny.game.data.cache;

import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.javassist.*;
import com.tny.game.common.utils.*;
import com.tny.game.data.annotation.*;
import com.tny.game.data.cache.exception.*;

import java.lang.reflect.*;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 12:23 下午
 */
public class AnnotationEntityKeyMaker<K extends Comparable<K>> implements EntityKeyMaker<K, Object> {

	private final Class<?> cacheClass;

	private final Class<?> keyClass;

	private PropertyAccessor propertyAccessor;

	private MethodAccessor methodAccessor;

	public AnnotationEntityKeyMaker(Class<?> cacheClass) {
		this.cacheClass = cacheClass;
		List<Field> fields = ReflectAide.getDeepFieldsByAnnotation(cacheClass, EntityId.class);
		List<Method> methods = ReflectAide.getDeepMethodsByAnnotation(cacheClass, EntityId.class);
		ClassAccessor accessor = JavassistAccessors.getGClass(cacheClass);
		Asserts.checkState(!fields.isEmpty() || !methods.isEmpty(), "Class {} 不存在标记有 {} 注解的字段或方法", cacheClass, EntityId.class);
		if (!fields.isEmpty()) {
			Asserts.checkState(fields.size() == 1, "Class {} 存在标记 {} 注解的字段 {} 数量>1", cacheClass, EntityId.class, fields);
			Field field = fields.get(0);
			this.propertyAccessor = accessor.getProperty(field.getName());
			this.keyClass = propertyAccessor.getPropertyType();
		} else if (!methods.isEmpty()) {
			Asserts.checkState(methods.size() == 1, "Class {} 存在标记 {} 注解的方法 {} 数量>1", cacheClass, EntityId.class, fields);
			Method method = methods.get(0);
			this.methodAccessor = accessor.getMethod(method);
			this.keyClass = method.getReturnType();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public K make(Object object) {
		try {
			if (propertyAccessor != null) {
				return as(propertyAccessor.getPropertyValue(object));
			} else {
				return as(methodAccessor.invoke(object));
			}
		} catch (InvocationTargetException e) {
			throw new GetCacheIdException(e, "{} get id exception", this.cacheClass);
		}
	}

	@Override
	public Class<K> getKeyClass() {
		return as(this.keyClass);
	}

}
