package com.tny.game.base.item;

import java.util.HashSet;
import java.util.Set;

/**
 * Owner构建器
 *
 * @param <I>
 * @param <O>
 * @param <B>
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public abstract class OwnerBuilder<I extends Stuff<?>, O extends StuffOwner<?, I>, B extends OwnerBuilder<I, O, B>> {

    /**
     * 玩家ID
     */
    protected long playerID;

    /**
     * 物品模型列表
     */
    protected Set<I> itemSet = new HashSet<I>();

    /**
     * 设置玩家id
     *
     * @param playerID 玩家id
     * @return 返回ownerBuilder对象
     */
    public B setPlayerID(long playerID) {
        this.playerID = playerID;
        return (B) this;
    }

    /**
     * 添加item对象
     *
     * @param item 添加的item对象
     * @return 返回ownerBuilder对象
     */
    public B addItem(I item) {
        this.itemSet.add(item);
        return (B) this;
    }

    /**
     * 添加item对象列表
     *
     * @param itemList 添加的item对象列表
     * @return 返回ownerBuilder对象
     */
    public B addItem(Set<I> itemList) {
        for (I item : itemList)
            this.itemSet.add(item);
        return (B) this;
    }

    /**
     * 构建owner对象
     *
     * @return owner对象
     */
    public O build() {
        StuffOwner<?, I> entity = createItemOwner();
        entity.setPlayerID(playerID);
        for (I item : this.itemSet) {
            entity.itemMap.put(item.getID(), item);
        }
        return (O) entity;
    }

    /**
     * 获取构建owner
     *
     * @return
     */
    protected abstract StuffOwner<?, I> createItemOwner();

}
