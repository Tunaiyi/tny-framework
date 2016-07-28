package com.tny.game.suite.base;

import java.util.Collection;
import java.util.Collections;

/**
 * 没有任何数据库存储的manager
 *
 * @param <O>
 * @author KGTny
 */
public abstract class GameLocalManager<O> extends GameManager<O> {

    protected GameLocalManager(Class<? extends O> entityClass) {
        super(entityClass);
    }

    @Override
    public boolean save(O item) {
        return true;
    }

    @Override
    public Collection<O> save(Collection<O> itemCollection) {
        return Collections.emptyList();
    }

    @Override
    public boolean update(O item) {
        return true;
    }

    @Override
    public Collection<O> update(Collection<O> itemCollection) {
        return Collections.emptyList();
    }

    @Override
    public boolean insert(O item) {
        return true;
    }

    @Override
    public Collection<O> insert(Collection<O> itemCollection) {
        return Collections.emptyList();
    }

    @Override
    public boolean delete(O item) {
        return true;
    }

    @Override
    public Collection<O> delete(Collection<O> itemCollection) {
        return Collections.emptyList();
    }

}
