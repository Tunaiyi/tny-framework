package com.tny.game.base.item;


public interface TradeItem<I extends ItemModel> extends DealedItem<I> {

    public AlterType getAlertType();

}
