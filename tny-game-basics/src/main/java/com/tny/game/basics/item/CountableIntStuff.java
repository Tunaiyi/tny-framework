package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

public abstract class CountableIntStuff<SM extends CountableStuffModel> extends BaseCountableStuff<SM, Integer> {

	protected int number;

	@Override
	public Integer getNumberLimit() {
		Number number = this.model.countNumberLimit(this);
		if (number == null) {
			return null;
		}
		return number.intValue();
	}

	@Override
	public Integer getNumber() {
		return this.number;
	}

	@Override
	protected void deduct(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
		int alter = this.getDeductAlterType(tradeItem).deduct(this, tradeItem.getNumber()).intValue();
		if (alter > 0) {
			int oldNumber = this.getNumber();
			this.number -= alter;
			this.postDeduct(alter, oldNumber, this.number, action, attributes);
		}
	}

	@Override
	protected void reward(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
		int alter = this.getRewardAlterType(tradeItem).reward(this, tradeItem.getNumber()).intValue();
		if (alter > 0) {
			int oldNumber = this.getNumber();
			this.number += alter;
			this.postReward(alter, oldNumber, this.number, action, attributes);
		}
	}

	@Override
	protected void setNumber(Integer number) {
		this.number = number;
	}

}
