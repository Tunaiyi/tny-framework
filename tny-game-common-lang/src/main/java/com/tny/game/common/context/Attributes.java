package com.tny.game.common.context;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author KGTny
 * @ClassName: Attributes
 * @Description: 属性对象接口
 * @date 2011-9-21 ����10:48:40
 * <p>
 * 属性对象接口
 * <p>
 * <br>
 */
public interface Attributes {

	/**
	 * 获取指定key和类型的属性
	 * <p>
	 * <p>
	 * 获取指定key和类型的属性<br>
	 *
	 * @param key 取指定key
	 * @return 有该属性返回该属性 否则返回null
	 */
	<T> T getAttribute(AttrKey<? extends T> key);

	/**
	 * 获取指定key和类型的属性
	 * <p>
	 * <p>
	 * 获取指定key和类型的属性<br>
	 *
	 * @param key          取指定key
	 * @param defaultValue 默认值
	 * @return 有该属性返回该属性 否则返回 defaultValue
	 */
	<T> T getAttribute(AttrKey<? extends T> key, T defaultValue);

	/**
	 * 插入指定的 key 与 value, 如果key在返回存在的值, 不存在插入value并返回value
	 *
	 * @param key   指定key
	 * @param value 默认值
	 * @return 如果在返回存在的值, 不存在插入value并返回value
	 */
	<T> T computeIfAbsent(AttrKey<? extends T> key, T value);

	/**
	 * 获取指定key的值, 如果在返回存在的值, 不存在插入supplier返回的值,并返回该值
	 *
	 * @param key      指定key
	 * @param supplier 默认值创建器
	 * @return 如果在返回存在的值, 不存在插入value并返回value
	 */
	<T> T computeIfAbsent(AttrKey<? extends T> key, Supplier<T> supplier);

	/**
	 * 插入指定的 key 与 value, 如果key在返回存在的值, 不存在插入value并返回null
	 *
	 * @param key   指定key
	 * @param value 默认值
	 * @return 如果在返回存在的值, 不存在插入value并返回value
	 */
	<T> T setIfAbsent(AttrKey<? extends T> key, T value);

	/**
	 * 删除指定key的属性
	 * <p>
	 * <p>
	 * 删除指定key的属性<br>
	 *
	 * @param key 指定的Key
	 */
	<T> T removeAttribute(AttrKey<? extends T> key);

	/**
	 * 设置key和属性
	 * <p>
	 * <p>
	 * 设置key和属性<br>
	 *
	 * @param key   设置的key
	 * @param value 设置的属性
	 */
	<T> void setAttribute(AttrKey<? extends T> key, T value);

	/**
	 * 批量设置属性
	 * <p>
	 * <p>
	 * 批量设置属性 <br>
	 *
	 * @param map 设置的属性Map
	 */
	void setAttribute(Map<AttrKey<?>, ?> map);

	/**
	 * 设置key和属性<br>
	 *
	 * @param entry
	 */
	void setAttribute(AttrEntry<?> entry);

	/**
	 * 批量设置属性
	 * <p>
	 * <p>
	 * 批量设置属性 <br>
	 */
	void setAttribute(Collection<AttrEntry<?>> entries);

	/**
	 * 批量设置属性
	 * <p>
	 * <p>
	 * 批量设置属性 <br>
	 */
	void setAttribute(AttrEntry<?>... entries);

	/**
	 * 删除指定key集合的属性
	 * <p>
	 * <p>
	 * 删除指定key集合的属性<br>
	 *
	 * @param keys 指定key集合的属性
	 */
	void removeAttribute(Collection<AttrKey<?>> keys);

	/**
	 * 获取所有的属性键值对
	 *
	 * @return
	 */
	Map<AttrKey<?>, Object> getAttributeMap();

	/**
	 * 删除所有的属性键值对
	 *
	 * @return
	 */
	void clearAttribute();

	/**
	 * 是否为空
	 *
	 * @return
	 */
	boolean isEmpty();

}
