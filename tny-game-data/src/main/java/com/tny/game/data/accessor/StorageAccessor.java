package com.tny.game.data.accessor;

import java.util.*;

/**
 * @author KGTny
 */
public interface StorageAccessor<K extends Comparable<?>, O> {

	O get(K key);

	Map<K, ? extends O> get(Collection<? extends K> ids);

	/**
	 * 插入
	 *
	 * @param object 存储对象
	 * @return 成功返回 true 失败返回 false
	 */
	boolean insert(O object);

	/**
	 * 批量插入
	 *
	 * @param objects 存储对象
	 * @return 返回失败列表
	 */
	Collection<O> insert(Collection<O> objects);

	/**
	 * 更新如果存在则更新
	 *
	 * @param object 对象
	 * @return 成功返回 true 失败返回 false
	 */
	boolean update(O object);

	/**
	 * 批量更新如果存在则更新
	 *
	 * @param objects 对象列表
	 * @return 返回失败列表
	 */
	Collection<O> update(Collection<O> objects);

	/**
	 * 删除
	 *
	 * @param object 删除对象
	 */
	void delete(O object);

	/**
	 * 批量删除
	 *
	 * @param objects 删除对象
	 */
	void delete(Collection<O> objects);

	/**
	 * 保存
	 *
	 * @param object 保存对象
	 * @return 成功返回 true 失败返回 false
	 */
	boolean save(O object);

	/**
	 * 批量保存
	 *
	 * @param objects 保存对象列表
	 * @return 返回失败列表
	 */
	Collection<O> save(Collection<O> objects);

}
