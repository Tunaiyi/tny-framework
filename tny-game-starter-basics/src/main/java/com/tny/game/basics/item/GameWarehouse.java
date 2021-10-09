package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

import static com.tny.game.common.utils.ObjectAide.*;

public class GameWarehouse extends AbstractWarehouse<GameStuffOwner<?, ?, ?>> {

	@Override
	protected void consume(Trade result, AttrEntry<?>... entries) {
		super.consume(result, entries);
	}

	@Override
	protected void consume(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
		super.consume(tradeItem, action, entries);
	}

	@Override
	protected void receive(Trade result, AttrEntry<?>... entries) {
		super.receive(result, entries);
	}

	@Override
	protected void receive(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
		super.receive(tradeItem, action, entries);
	}

	@Override
	protected void doReceive(GameStuffOwner<?, ?, ?> owner, TradeItem<?> tradeItem, Action action, Attributes attributes) {
		owner.receive(as(tradeItem), action, attributes);
	}

	@Override
	protected void doConsume(GameStuffOwner<?, ?, ?> owner, TradeItem<?> tradeItem, Action action, Attributes attributes) {
		owner.consume(as(tradeItem), action, attributes);
	}

}
