package com.tny.game.base.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 抽象事物拥有者对象
 *
 * @param <IM>
 * @param <S>
 * @author KGTny
 */
public abstract class StuffOwner<IM extends ItemModel, S extends Stuff<IM>> implements Owner<IM, S> {

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
    protected ConcurrentMap<Long, S> itemMap = new ConcurrentHashMap<Long, S>();

    @Override
    public long getPlayerID() {
        return playerID;
    }

    @Override
    public S getItemByID(long id) {
        return itemMap.get(id);
    }

    @Override
    public List<S> getItemByItemID(int id) {
        List<S> stuffList = new ArrayList<S>();
        S stuff = itemMap.get((long) id);
        if (stuff != null)
            stuffList.add(stuff);
        return stuffList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <IT extends ItemType> IT getOwnerItemType() {
        return (IT) ownItemType;
    }

    public Collection<S> getAllItemCollection() {
        return Collections.unmodifiableCollection(itemMap.values());
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
