package com.tny.game.suite.base;

import com.tny.game.cache.async.AsyncCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GameCacheManager<O> extends GameManager<O> {

    @Autowired
    @Qualifier("asyncCache")
    protected AsyncCache cache;

    protected GameCacheManager(Class<? extends O> entityClass) {
        super(entityClass);
    }

    @Override
    protected O get(long playerID, Object... objects) {
        return this.get(playerID, this.entityClass, objects);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<O> getObjects(long playerID, Collection<?> ids) {
        List<String> keys = ids.stream().map(id -> this.cache.getKey(this.entityClass, playerID, id)).collect(Collectors.toList());
        return (Collection<O>) this.cache.getObjectsByKeys(this.entityClass, keys);
    }

    protected O getByCache(Object... objects) {
        return this.cache.getObject(this.entityClass, objects);
    }

    protected <OO> OO get(long playerID, Class<OO> clazzClass, Object... objects) {
        if (objects.length == 0) {
            return this.cache.getObject(clazzClass, playerID);
        } else {
            Object[] param = new Object[objects.length + 1];
            param[0] = playerID;
            System.arraycopy(objects, 0, param, 1, objects.length);
            return this.cache.getObject(clazzClass, param);
        }
    }

    @Override
    public boolean save(O item) {
        return this.cache.setObject(item);
    }

    @Override
    public Collection<O> save(Collection<O> itemCollection) {
        return this.cache.setObject(itemCollection);
    }

    @Override
    public boolean update(O item) {
        return this.cache.updateObject(item);
    }

    @Override
    public Collection<O> update(Collection<O> itemCollection) {
        return itemCollection.stream().filter(o -> !this.update(o)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean insert(O item) {
        return this.cache.addObject(item);
    }

    @Override
    public Collection<O> insert(Collection<O> itemCollection) {
        return itemCollection.stream().filter(o -> !this.insert(o)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean delect(O item) {
        return this.cache.deleteObject(item);
    }

    @Override
    public Collection<O> delect(Collection<O> itemCollection) {
        return itemCollection.stream().filter(o -> !this.delect(o)).collect(Collectors.toCollection(ArrayList::new));
    }

}
