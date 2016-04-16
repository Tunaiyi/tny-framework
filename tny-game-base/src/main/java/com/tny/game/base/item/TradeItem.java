package com.tny.game.base.item;


public interface TradeItem<I extends ItemModel> extends DealedItem<I> {

    AlterType getAlertType();

    @SuppressWarnings("unchecked")
    default <IM extends I> TradeItem<IM> as() {
        return (TradeItem<IM>) this;
    }

}
