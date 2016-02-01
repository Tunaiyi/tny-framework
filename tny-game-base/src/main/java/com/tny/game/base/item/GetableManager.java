package com.tny.game.base.item;

import java.util.Collection;

/**
 * 可获取的管理器
 *
 * @param <O>
 * @author KGTny
 */
public abstract class GetableManager<O> implements Manager<O> {

    /**
     * 获取玩家的对象
     *
     * @param playerID 玩家id
     * @param itemID   itemID
     * @param object   附加参数
     * @return 返回对象
     */
    protected abstract O get(long playerID, Object... object);

    /**
     * 获取玩家的对象列表
     *
     * @param playerID 玩家id
     * @param itemIDs  id列表
     * @param object   附加参数
     * @return 返回对象集合
     */
    protected abstract Collection<O> getObjects(long playerID, Collection<?> itemIDs);

}
