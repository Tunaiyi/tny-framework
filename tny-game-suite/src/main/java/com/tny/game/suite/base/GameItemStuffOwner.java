package com.tny.game.suite.base;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class GameItemStuffOwner<IM extends ItemModel, SM extends ItemModel, S extends Stuff<?>> extends GameItem<IM> implements StuffOwner<IM, S> {

	/**
	 * 扣除事物
	 *
	 * @param tradeItem  交易对象
	 * @param action     交易行为
	 * @param attributes 参数
	 */
	protected abstract void consume(TradeItem<SM> tradeItem, Action action, Attributes attributes);

	/**
	 * 添加事物
	 *
	 * @param tradeItem  交易对象
	 * @param action     交易行为
	 * @param attributes 参数
	 */
	protected abstract void receive(TradeItem<SM> tradeItem, Action action, Attributes attributes);

}
