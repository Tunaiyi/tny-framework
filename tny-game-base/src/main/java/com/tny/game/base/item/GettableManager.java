package com.tny.game.base.item;

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
     * @param object   附加参数
     * @return 返回对象
     */
    protected abstract O get(long playerId, Object... object);

    /**
     * 获取玩家的对象列表
     *
     * @param playerId 玩家id
     * @param itemIDs  id列表
     * @return 返回对象集合
     */
    protected abstract Collection<O> gets(long playerId, Collection<?> itemIDs);

}
