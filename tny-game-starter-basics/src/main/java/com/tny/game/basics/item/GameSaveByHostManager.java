package com.tny.game.basics.item;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 存储当前存储的对象存储在另一个对象上的Manager
 *
 * @param <O>
 * @param <H>
 * @author KGTny
 */
public abstract class GameSaveByHostManager<O, H, M extends QueryManager<H>> extends GameManager<O> {

    protected GameSaveByHostManager(Class<? extends O> entityClass) {
        super(entityClass);
    }

    @Override
    protected O get(long playerId, long id) {
        H saveObject = findHost(playerId, id);
        if (saveObject == null) {
            return null;
        }
        return hostToItem(saveObject, id);
    }

    @Override
    protected O get(AnyId id) {
        H saveObject = findHost(id);
        if (saveObject == null) {
            return null;
        }
        return hostToItem(saveObject, id.getId());
    }

    @Override
    protected List<O> get(Collection<AnyId> anyIdList) {
        return anyIdList.stream().map(this::get).collect(Collectors.toList());
    }

    @Override
    protected List<O> find(Map<String, Object> query) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<O> findAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * 通过目标id 查找宿主对象
     *
     * @param id 目标id
     * @return 返回宿主对象
     */
    protected abstract H findHost(AnyId id);

    /**
     * 通过目标playerId, id 查找宿主对象
     *
     * @param playerId 目标玩家id
     * @param id       目标id
     * @return 返回宿主对象
     */
    protected abstract H findHost(long playerId, long id);

    /**
     * 宿主对象获取目标对象
     *
     * @param host 宿主(存储)对象
     * @param id   目标对象 id
     * @return 目标对象
     */
    protected abstract O hostToItem(H host, long id);

    /**
     * 获取 item 对象的宿主(存储)对象
     *
     * @param item 实例
     * @return 返回宿主(存储)对象
     */
    protected abstract H itemToHost(O item);

    /**
     * 获取 item 存储所在的对象的manager
     *
     * @return 返回存储的实例的 manager
     */
    protected abstract M manager();

    @Override
    public boolean save(O item) {
        H object = this.itemToHost(item);
        Manager<H> manager = this.manager();
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
        H object = this.itemToHost(item);
        Manager<H> manager = this.manager();
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
    public void delete(O item) {
    }

    @Override
    public void delete(Collection<O> itemCollection) {
    }

}
