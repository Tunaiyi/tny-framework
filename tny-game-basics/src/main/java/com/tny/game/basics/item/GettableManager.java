package com.tny.game.basics.item;

import java.util.Collection;

/**
 * 可获取的管理器
 *
 * @param <O>
 * @author KGTny
 */
public abstract class GettableManager<O> implements Manager<O> {

	/**
	 * 获取玩家的对象
	 *
	 * @param playerId 玩家id
	 * @return 返回对象
	 */
	protected abstract O get(long playerId);

	/**
	 * 获取玩家的对象
	 *
	 * @param playerId 玩家id
	 * @param id       item的id
	 * @return 返回对象
	 */
	protected abstract O get(long playerId, long id);

	/**
	 * 获取玩家的对象列表
	 *
	 * @param playerId 玩家id
	 * @param itemIds  id列表
	 * @return 返回对象集合
	 */
	protected abstract Collection<O> getAll(long playerId, Collection<Long> itemIds);

}