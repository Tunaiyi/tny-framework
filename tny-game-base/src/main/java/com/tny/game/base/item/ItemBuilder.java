package com.tny.game.base.item;

import com.tny.game.common.utils.Throws;

/**
 * item构建器
 *
 * @param <I>
 * @param <IM>
 * @param <B>
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public abstract class ItemBuilder<I extends AbstractItem<IM>, IM extends ItemModel, B extends ItemBuilder<I, IM, B>> {

    /**
     * 玩家ID
     */
    protected long playerId;

    /**
     * 物品模型
     */
    protected IM itemModel;

    /**
     * 设置玩家ID <br>
     *
     * @param playerId 玩家id
     * @return 构建器
     */
    public B setPlayerId(long playerId) {
        this.playerId = playerId;
        return (B) this;
    }

    /**
     * 设置事物模型 <br>
     *
     * @param model 物品模型
     * @return 构建器
     */
    public B setModel(IM model) {
        Throws.checkNotNull(model);
        this.itemModel = model;
        return (B) this;
    }

    /**
     * 构建事物对象
     *
     * @return
     */
    public I build() {
        I entity = createItem();
        entity.setPlayerId(playerId);
        entity.setModel(itemModel);
        afterModelItem(entity);
        return entity;
    }

    /**
     * 构建事物对象
     *
     * @return
     */
    protected void afterModelItem(I item) {

    }

    /**
     * 构建事物对象
     *
     * @return
     */
    protected abstract I createItem();

}
