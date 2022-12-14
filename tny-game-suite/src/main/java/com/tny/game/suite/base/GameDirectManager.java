package com.tny.game.suite.base;

import com.tny.game.cache.*;
import org.slf4j.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class GameDirectManager<O> extends GameManager<O> {

    public static final Logger LOGGER = LoggerFactory.getLogger(GameDirectManager.class);

    protected DirectCache cache;

    private Consumer<O> onLoad;

    protected GameDirectManager(DirectCache cache, Class<? extends O> entityClass) {
        this(cache, entityClass, null);
    }

    protected GameDirectManager(DirectCache cache, Class<? extends O> entityClass, Consumer<O> onLoad) {
        super(entityClass);
        this.onLoad = onLoad;
        this.cache = cache;
    }

    @Override
    protected O get(long playerId, Object... objects) {
        return onLoad(this.get(playerId, this.entityClass, objects));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<O> gets(long playerId, Collection<?> ids) {
        List<String> keys = ids.stream().map(id -> this.cache.getKey(this.entityClass, playerId, id)).collect(Collectors.toList());
        return onLoad((Collection<O>)this.cache.getObjectsByKeys(this.entityClass, keys));
    }

    @SuppressWarnings("unchecked")
    protected Collection<O> getByKeys(String... keys) {
        return onLoad((Collection<O>)this.cache.getObjectsByKeys(this.entityClass, Arrays.asList(keys)));
    }

    @SuppressWarnings("unchecked")
    protected Collection<O> getByKeys(Collection<String> keys) {
        return onLoad((Collection<O>)this.cache.getObjectsByKeys(this.entityClass, keys));
    }

    @SuppressWarnings("unchecked")
    protected O getByKey(String key) {
        return onLoad(this.cache.getObjectByKey(this.entityClass, key));
    }

    protected O getBy(Object... objects) {
        return onLoad(this.cache.getObject(this.entityClass, objects));
    }

    private <OO> OO get(long playerId, Class<OO> clazzClass, Object... objects) {
        if (objects.length == 0) {
            return this.cache.getObject(clazzClass, playerId);
        } else {
            Object[] param = new Object[objects.length + 1];
            param[0] = playerId;
            System.arraycopy(objects, 0, param, 1, objects.length);
            return this.cache.getObject(clazzClass, param);
        }
    }

    private O onLoad(O o) {
        if (o == null || this.onLoad == null) {
            return o;
        }
        try {
            this.onLoad.accept(o);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
        return o;
    }

    private Collection<O> onLoad(Collection<O> os) {
        if (os == null || os.isEmpty() || this.onLoad == null) {
            return os;
        }
        for (O o : os) {
            try {
                this.onLoad.accept(o);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
        return os;
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
    public boolean delete(O item) {
        return this.cache.deleteObject(item);
    }

    @Override
    public Collection<O> delete(Collection<O> itemCollection) {
        return itemCollection.stream().filter(o -> !this.delete(o)).collect(Collectors.toCollection(ArrayList::new));
    }

}
