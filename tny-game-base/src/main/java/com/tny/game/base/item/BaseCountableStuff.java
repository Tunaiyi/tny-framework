package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;

public abstract class BaseCountableStuff<SM extends CountableStuffModel, N extends Number> extends AbstractItem<SM> implements CountableStuff<SM, N> {

    @Override
    public boolean isNumberLimit() {
        return this.model.isNumberLimit();
    }

    @Override
    public boolean tryEnough(long costNum) {
        return !AlterType.CHECK.overLowerLimit(this, costNum);
    }

    //	/**
    //	 * 构件一部分数量的物品。
    //	 *
    //	 * @param number
    //	 *            构件数量
    //	 * @return 返回构件数量的物品
    //	 */
    //	protected abstract <S extends Stuff<SM>> S createPart(long number);

    protected boolean isLack(N costNum, AlterType alterType) {
        return alterType.overLowerLimit(this, costNum);
    }

    protected boolean isExcess(N receiveNum, AlterType alterType) {
        return alterType.overLowerLimit(this, receiveNum);
    }

    protected abstract TradeResult consume(Action action, TradeItem<? extends SM> tradeItem);

    protected abstract TradeResult receive(Action action, TradeItem<? extends SM> tradeItem);

    protected abstract void setNumber(N number);

    protected AlterType getReceiveAlterType(TradeItem<? extends SM> item) {
        return item.getAlertType();
    }

    protected AlterType getConsumeAlterType(TradeItem<? extends SM> item) {
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
    protected abstract TradeResult postConsume(N alter, N oldNumber, N current, Action action);

    /**
     * 执行增加
     *
     * @param alter
     * @param oldNumber
     * @param current
     * @param action
     */
    protected abstract TradeResult postReceive(N alter, N oldNumber, N current, Action action);

}
