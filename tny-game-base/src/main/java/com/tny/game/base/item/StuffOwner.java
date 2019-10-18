package com.tny.game.base.item;

import com.tny.game.common.collection.*;

import java.util.*;

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
    protected long playerId;

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
    public long getPlayerId() {
        return playerId;
    }

    @Override
    public S getItemById(long id) {
        return itemMap.get(id);
    }

    @Override
    public S getItemByItemId(int itemId) {
        return itemMap.get((long) itemId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ItemType getItemType() {
        return ownItemType;
    }

    protected Collection<S> getItemCollection() {
        return Collections.unmodifiableCollection(itemMap.values());
    }

    protected void setItemCollection(Collection<S> collection) {
        itemMap.clear();
        for (S stuff : collection)
            itemMap.put(stuff.getId(), stuff);
    }

    protected void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
