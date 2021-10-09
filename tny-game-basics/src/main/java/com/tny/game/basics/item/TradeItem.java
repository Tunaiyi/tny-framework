package com.tny.game.basics.item;

/**
 * 将要交易的Item信息
 *
 * @param <I> ItemModel类型
 */
public interface TradeItem<I extends ItemModel> extends DealItem<I> {

	AlterType getAlertType();

	boolean isValid();

	@SuppressWarnings("unchecked")
	default <IM extends I> TradeItem<IM> as() {
		return (TradeItem<IM>)this;
	}

}
