package com.tny.game.suite.base;

import com.tny.game.basics.item.*;

import java.util.*;

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
    protected O get(long playerId, Object... objects) {
        return getInstance(playerId, objects);
    }

    protected abstract O getInstance(long playerId, Object... object);

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
        List<O> list = new ArrayList<>();
        for (O item : itemCollection) {
            if (!this.save(item)) {
                list.add(item);
            }
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
        List<O> list = new ArrayList<>();
        for (O item : itemCollection) {
            if (!this.update(item)) {
                list.add(item);
            }
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
