package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 存储当前存储的Item存储在一个Owner上的Manager
 *
 * @author KGTny
 */
public abstract class GameSaveByOwnerManager<S extends Stuff<?>, O extends Owner<?, S>> extends GameSaveByOtherManager<S, O> {

    @Autowired
    private GameWarehouseManager gameWarehouseManager;

    private ItemType ownItemType;

    protected GameSaveByOwnerManager(Class<? extends S> entityClass, ItemType ownItemType) {
        super(entityClass);
        this.ownItemType = ownItemType;
    }

    @Override
    protected O getSaveObject(S item) {
        return this.getSaveObject(item.getPlayerID());
    }

    @SuppressWarnings("unchecked")
    protected O getSaveObject(long playerID) {
        Warehouse holder = this.gameWarehouseManager.getWarehouse(playerID);
        if (holder == null)
            return null;
        return (O) holder.getOwner(this.ownItemType, Owner.class);
    }

    @Override
    protected S get(long playerID, Object... object) {
        O owner = this.getSaveObject(playerID);
        return owner.getItemByID((long) object[0]);
    }

    @Override
    protected abstract Manager<O> getManager(S item);

    @Override
    protected S getInstance(long playerID, Object... object) {
        O owner = this.getSaveObject(playerID);
        return owner.getItemByID((long) object[0]);
    }
}
