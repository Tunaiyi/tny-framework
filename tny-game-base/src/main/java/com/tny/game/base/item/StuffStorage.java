package com.tny.game.base.item;

import com.tny.game.common.concurrent.collection.*;

import java.util.*;

/**
 * 抽象事物拥有者对象
 *
 * @param <IM>
 * @param <S>
 * @author KGTny
 */
public abstract class StuffStorage<IM extends ItemModel, SM extends ItemModel, S extends Stuff<SM>> implements Storage<IM, S> {

    /**
     * 玩家id
     */
    //	@CacheID(index = 0)
    protected long playerId;

    /**
     * 模型
     */
    protected IM model;

    /**
     * 拥有的item模型
     */
    //	@Link(name = "itemCollection", ignore = true, ignoreOperation = { Operation.SAVE, Operation.DELETE, Operation.UPDATE })
    protected Map<Long, S> itemMap = new CopyOnWriteMap<>();

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    @Override
    public S getItemById(long id) {
        return this.itemMap.get(id);
    }

    @Override
    public S getItemByItemId(int itemId) {
        return this.itemMap.get((long)itemId);
    }

    @Override
    public ItemType getItemType() {
        return this.model.getItemType();
    }

    protected Collection<S> getItemCollection() {
        return Collections.unmodifiableCollection(this.itemMap.values());
    }

    protected void setItemCollection(Collection<S> collection) {
        this.itemMap.clear();
        for (S stuff : collection)
            this.itemMap.put(stuff.getId(), stuff);
    }

    protected void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

}
