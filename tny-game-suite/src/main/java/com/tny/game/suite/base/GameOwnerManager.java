package com.tny.game.suite.base;

import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 存储当前存储的Item存储在一个Owner上的Manager
 *
 * @param <O>
 * @author KGTny
 */
public abstract class GameOwnerManager<O extends GameItemOwner> extends GameCacheManager<O> {

    private Class<?>[] saveItemClasses = new Class<?>[0];

    private ItemType itemType;

    protected GameOwnerManager(Class<? extends O> entityClass, ItemType itemType, Collection<? extends Class<?>> classCollection) {
        super(entityClass);
        this.itemType = itemType;
        this.saveItemClasses = classCollection.toArray(this.saveItemClasses);
    }

    public O getOwner(long playerID) {
        if (playerID == 0L)
            return null;
        return this.get(playerID);
    }

    @Override
    protected O get(long playerID, Object... object) {
        return super.get(playerID, this.itemType.getID());
    }

    @Override
    public boolean save(O item) {
        if (this.entityClass.isInstance(item))
            return super.save(item);
        if (!this.isSaveItem(item))
            return false;
        if (item instanceof Item) {
            O owner = this.getOwner(item.getPlayerID());
            if (owner != null)
                return super.save(owner);
        }
        return false;
    }

    @Override
    public Collection<O> save(Collection<O> itemCollection) {
        Collection<O> saveCollection = new ArrayList<>();
        for (O item : itemCollection) {
            if (this.entityClass.isInstance(item))
                saveCollection.add(item);
            if (!this.isSaveItem(item))
                continue;
            if (item instanceof Item) {
                O owner = this.getOwner(item.getPlayerID());
                if (owner != null)
                    saveCollection.add(item);
            }
        }
        return super.save(saveCollection);
    }

    private boolean isSaveItem(Object object) {
        for (Class<?> clazz : this.saveItemClasses) {
            if (clazz.isInstance(object))
                return true;
        }
        return false;
    }

}
