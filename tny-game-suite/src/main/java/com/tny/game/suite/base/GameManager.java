package com.tny.game.suite.base;

import com.tny.game.base.item.*;

import java.util.Collection;

public abstract class GameManager<O> extends GettableManager<O> {

    protected final Class<O> entityClass;

    protected GameManager(Class<? extends O> entityClass) {
        super();
        this.entityClass = (Class<O>) entityClass;
    }

    /**
     * 获取玩家的对象
     *
     * @param playerId 玩家id
     * @param object   附加参数
     * @return 返回对象
     */
    @Override
    protected abstract O get(long playerId, Object... object);

    /**
     * 获取玩家的对象列表
     *
     * @param playerId 玩家id
     * @param itemIDs  id列表
     * @return 返回对象集合
     */
    @Override
    protected abstract Collection<O> gets(long playerId, Collection<?> itemIDs);
}