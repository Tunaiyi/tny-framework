package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

/**
 * 抽象物品管理器
 *
 * @param <SM>
 * @param <S>
 * @author KGTny
 */
public abstract class AbstractCountableOwner<IM extends ItemModel, SM extends CountableStuffModel<?>, S extends BaseCountableStuff<SM, ?>>
		extends Owner<IM, SM, S> {

	protected void doConsume(S stuff, Action action, TradeItem<SM> tradeItem, Attributes attributes) {
		stuff.consume(action, tradeItem, attributes);
	}

	protected void doReceive(S stuff, Action action, TradeItem<SM> tradeItem, Attributes attributes) {
		stuff.receive(action, tradeItem, attributes);
	}

	/**
	 * 构建item <br>
	 *
	 * @param oim
	 * @return
	 */
	protected abstract <OIM extends ItemModel> S createStuff(OIM oim);

}
