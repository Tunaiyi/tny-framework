package com.tny.game.suite.base;

import com.tny.game.base.item.GetableManager;

public abstract class GameManager<O> extends GetableManager<O> {

    protected final Class<? extends O> entityClass;

    protected GameManager(Class<? extends O> entityClass) {
        super();
        this.entityClass = entityClass;
    }

    protected O getObject(long playerID, Object... object) {
        return this.get(playerID, object);
    }

}