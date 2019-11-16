package com.tny.game.suite.base;

import com.tny.game.base.item.*;

import javax.annotation.Resource;

/**
 * 存储当前存储的Item存储在一个Storage上的Manager
 *
 * @author KGTny
 */
public abstract class GameSaveByStorageManager<S extends Stuff<?>, O extends Storage<?, S>> extends GameSaveByOtherManager<S, O> {

    @Resource
    private GameWarehouseManager gameWarehouseManager;

    private ItemType ownItemType;

    protected GameSaveByStorageManager(Class<? extends S> entityClass, ItemType ownItemType) {
        super(entityClass);
        this.ownItemType = ownItemType;
    }

    @Override
    protected O getSaveObject(S item) {
        return this.getSaveObject(item.getPlayerId());
    }

    @SuppressWarnings("unchecked")
    protected O getSaveObject(long playerID) {
        Warehouse holder = this.gameWarehouseManager.getWarehouse(playerID);
        if (holder == null)
            return null;
        return (O) holder.getStorage(this.ownItemType, Storage.class);
    }

    @Override
    protected S get(long playerID, Object... object) {
        O storage = this.getSaveObject(playerID);
        return storage.getItemById(((Number) object[0]).longValue());
    }

    @Override
    protected abstract Manager<O> getManager(S item);

    @Override
    protected S getInstance(long playerID, Object... object) {
        O storage = this.getSaveObject(playerID);
        return storage.getItemById((long) object[0]);
    }
}
