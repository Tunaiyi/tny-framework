package com.tny.game.data.cache;

/**
 * 获取
 * <p>
 */
public interface ObjectCache<K extends Comparable<?>, O> {

	/**
	 * @return 缓存
	 */
	CacheScheme getScheme();

	/**
	 * 通过 key 获取 对象
	 *
	 * @param key 键值
	 * @return 返回
	 */
	O get(K key);

	/**
	 * 放入缓存
	 *
	 * @param key    键值
	 * @param object 对象
	 */
	void put(K key, O object);

	/**
	 * 移除指定 key 值
	 *
	 * @param key    键值
	 * @param object 值
	 */
	boolean remove(K key, O object);

	/**
	 * @return 数量
	 */
	int size();

}
