package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class BaseStuffOwner<IM extends ItemModel, SM extends StuffModel, S extends Stuff<?>>
		extends BaseItem<IM>
		implements StuffOwner<IM, S> {

	/**
	 * 扣除事物
	 *
	 * @param tradeItem  交易对象
	 * @param action     交易行为
	 * @param attributes 参数
	 */
	protected abstract void deduct(TradeItem<SM> tradeItem, Action action, Attributes attributes);

	/**
	 * 添加事物
	 *
	 * @param tradeItem  交易对象
	 * @param action     交易行为
	 * @param attributes 参数
	 */
	protected abstract void reward(TradeItem<SM> tradeItem, Action action, Attributes attributes);

	/**
	 * 添加 Item
	 *
	 * @param stuffs
	 */
	protected abstract void setStuffs(Collection<S> stuffs);

}
