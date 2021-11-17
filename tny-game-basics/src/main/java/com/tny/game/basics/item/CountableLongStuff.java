package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

public abstract class CountableLongStuff<SM extends StuffModel<Long>> extends BaseCountableStuff<SM, Long> {

	protected long number;

	@Override
	public Long getNumberLimit() {
		return this.model.countNumberLimit(this);
	}

	@Override
	public Long getNumber() {
		return this.number;
	}

	@Override
	protected void consume(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
		long alter = this.getConsumeAlterType(tradeItem).consume(this, tradeItem.getNumber()).longValue();
		if (alter > 0) {
			long oldNumber = this.getNumber();
			this.number -= alter;
			this.postConsume(alter, oldNumber, this.number, action, attributes);
		}
	}

	@Override
	protected void receive(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
		long alter = this.getReceiveAlterType(tradeItem).receive(this, tradeItem.getNumber()).longValue();
		if (alter > 0) {
			long oldNumber = this.getNumber();
			this.number += alter;
			this.postReceive(alter, oldNumber, this.number, action, attributes);
		}
	}

	@Override
	protected void setNumber(Long number) {
		this.number = number;
	}

}
