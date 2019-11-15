package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.context.*;

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

    protected abstract void consume(Action action, TradeItem<SM> tradeItem, Attributes attributes);

    protected abstract void receive(Action action, TradeItem<SM> tradeItem, Attributes attributes);

    protected abstract void setNumber(N number);

    protected AlterType getReceiveAlterType(TradeItem<SM> item) {
        return item.getAlertType();
    }

    protected AlterType getConsumeAlterType(TradeItem<SM> item) {
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
    protected abstract void postConsume(N alter, N oldNumber, N current, Action action, Attributes attributes);

    /**
     * 执行增加
     *
     * @param alter
     * @param oldNumber
     * @param current
     * @param action
     */
    protected abstract void postReceive(N alter, N oldNumber, N current, Action action, Attributes attributes);

}
