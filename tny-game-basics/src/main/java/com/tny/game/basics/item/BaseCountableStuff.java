package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

public abstract class BaseCountableStuff<SM extends StuffModel<N>, N extends Number> extends BaseItem<SM> implements CountableStuff<SM, N> {

	@Override
	public boolean isNumberLimit() {
		return this.model.isNumberLimit();
	}

	@Override
	public boolean tryEnough(long costNum) {
		return !AlterType.CHECK.overLowerLimit(this, costNum);
	}

	protected boolean isLack(N costNum, AlterType alterType) {
		return alterType.overLowerLimit(this, costNum);
	}

	protected boolean isExcess(N receiveNum, AlterType alterType) {
		return alterType.overLowerLimit(this, receiveNum);
	}

	protected abstract void deduct(Action action, TradeItem<SM> tradeItem, Attributes attributes);

	protected abstract void reward(Action action, TradeItem<SM> tradeItem, Attributes attributes);

	protected abstract void setNumber(N number);

	protected AlterType getRewardAlterType(TradeItem<SM> item) {
		return item.getAlertType();
	}

	protected AlterType getDeductAlterType(TradeItem<SM> item) {
		return item.getAlertType();
	}

	/**
	 * 执行扣除
	 *
	 * @param alter
	 * @param oldNumber
	 * @param current
	 * @param action
	 */
	protected abstract void postDeduct(N alter, N oldNumber, N current, Action action, Attributes attributes);

	/**
	 * 执行增加
	 *
	 * @param alter
	 * @param oldNumber
	 * @param current
	 * @param action
	 */
	protected abstract void postReward(N alter, N oldNumber, N current, Action action, Attributes attributes);

}
