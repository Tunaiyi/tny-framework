package com.tny.game.common.utils;

import com.tny.game.base.item.Manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 存储当前存储的对象存储在另一个对象上的Manager
 *
 * @param <O>
 * @param <SO>
 * @author KGTny
 */
public abstract class GameSaveByOtherManager<O, SO> extends GameCacheManager<O> {

    protected GameSaveByOtherManager(Class<? extends O> entityClass) {
        super(entityClass);
    }

    @Override
    protected O get(long playerID, Object... objects) {
        return getInstance(playerID, objects);
    }

    protected abstract O getInstance(long playerID, Object... object);

    /**
     * 获取对应对象
     *
     * @param item
     * @return
     */
    protected abstract SO getSaveObject(O item);

    /**
     * 获取对应对象的manager
     *
     * @param item
     * @return
     */
    protected abstract Manager<SO> getManager(O item);

    @Override
    public boolean save(O item) {
        SO object = this.getSaveObject(item);
        Manager<SO> manager = this.getManager(item);
        if (object != null) {
            return manager.save(object);
        }
        return false;
    }

    @Override
    public Collection<O> save(Collection<O> itemCollection) {
        List<O> list = new ArrayList<O>();
        for (O item : itemCollection) {
            if (!this.save(item))
                list.add(item);
        }
        return list;
    }

    @Override
    public boolean update(O item) {
        SO object = this.getSaveObject(item);
        Manager<SO> manager = this.getManager(item);
        if (object != null) {
            return manager.update(object);
        }
        return false;
    }

    @Override
    public Collection<O> update(Collection<O> itemCollection) {
        List<O> list = new ArrayList<O>();
        for (O item : itemCollection) {
            if (!this.update(item))
                list.add(item);
        }
        return list;
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
