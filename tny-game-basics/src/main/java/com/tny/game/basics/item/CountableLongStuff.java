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
	protected void deduct(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
		long alter = this.getDeductAlterType(tradeItem).deduct(this, tradeItem.getNumber()).longValue();
		if (alter > 0) {
			long oldNumber = this.getNumber();
			this.number -= alter;
			this.postDeduct(alter, oldNumber, this.number, action, attributes);
		}
	}

	@Override
	protected void reward(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
		long alter = this.getRewardAlterType(tradeItem).reward(this, tradeItem.getNumber()).longValue();
		if (alter > 0) {
			long oldNumber = this.getNumber();
			this.number += alter;
			this.postReward(alter, oldNumber, this.number, action, attributes);
		}
	}

	@Override
	protected void setNumber(Long number) {
		this.number = number;
	}

}
