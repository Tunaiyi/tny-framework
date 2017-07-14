package com.tny.game.base.item;

import com.tny.game.common.collection.CopyOnWriteMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 抽象事物拥有者对象
 *
 * @param <IM>
 * @param <S>
 * @author KGTny
 */
public abstract class StuffOwner<IM extends ItemModel, SM extends ItemModel, S extends Stuff<SM>> implements Owner<IM, S> {

    /**
     * 玩家id
     */
    //	@CacheID(index = 0)
    protected long playerID;

    /**
     * 拥有事物类型
     */
    //	@CacheID(index = 1, name = "ownerItemID")
    protected ItemType ownItemType;

    protected IM model;

    /**
     * 拥有的item模型
     */
    //	@Link(name = "itemCollection", ignore = true, ignoreOperation = { Operation.SAVE, Operation.DELETE, Operation.UPDATE })
    protected Map<Long, S> itemMap = new CopyOnWriteMap<>();

    @Override
    public long getPlayerID() {
        return playerID;
    }

    @Override
    public S getItemByID(long id) {
        return itemMap.get(id);
    }

    @Override
    public S getItemByItemID(int itemID) {
        return itemMap.get((long) itemID);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <IT extends ItemType> IT getItemType() {
        return (IT) ownItemType;
    }

    protected Collection<S> getItemCollection() {
        return Collections.unmodifiableCollection(itemMap.values());
    }

    protected void setItemCollection(Collection<S> collection) {
        itemMap.clear();
        for (S stuff : collection)
            itemMap.put(stuff.getID(), stuff);
    }

    protected void setPlayerID(long playerID) {
        this.playerID = playerID;
    }
}
