package com.tny.game.base.item;

/**
 * 将要交易的Item信息
 *
 * @param <I> ItemModel类型
 */
public interface TradeItem<I extends ItemModel> extends DealedItem<I> {

    AlterType getAlertType();

    @SuppressWarnings("unchecked")
    default <IM extends I> TradeItem<IM> as() {
        return (TradeItem<IM>) this;
    }

}
