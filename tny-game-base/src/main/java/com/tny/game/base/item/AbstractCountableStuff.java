package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;

public abstract class AbstractCountableStuff<SM extends CountableStuffModel> extends AbstractItem<SM> implements CountableStuff<SM> {

    protected long number;

    @Override
    public boolean isNumberLimit() {
        return this.model.isNumberLimit();
    }

    @Override
    public long getNumberLimit() {
        return this.model.countNumberLimit(this);
    }

    @Override
    public long getNumber() {
        return this.number;
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

    protected boolean isLack(long costNum, AlterType alterType) {
        return alterType.overLowerLimit(this, costNum);
    }

    protected boolean isExcess(long reciveNum, AlterType alterType) {
        return alterType.overLowerLimit(this, reciveNum);
    }

    protected TradeResult consume(Action action, TradeItem<? extends SM> tradeItem) {
        long alter = this.getConsumeAlterType(tradeItem).consume(this, tradeItem.getNumber());
        if (alter > 0) {
            long oldNumber = this.getNumber();
            this.number -= alter;
            return this.postConsume(alter, oldNumber, this.number, action);
        }
        return TradeResult.empty();
    }

    protected TradeResult receive(Action action, TradeItem<? extends SM> tradeItem) {
        long alter = this.getReceiveAlterType(tradeItem).receive(this, tradeItem.getNumber());
        if (alter > 0) {
            long oldNumber = this.getNumber();
            this.number += alter;
            return this.postReceive(alter, oldNumber, this.number, action);
        }
        return TradeResult.empty();
    }

    protected void setNumber(long number) {
        this.number = number;
    }

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
    protected abstract TradeResult postConsume(long alter, long oldNumber, long current, Action action);

    /**
     * 执行增加
     *
     * @param alter
     * @param oldNumber
     * @param current
     * @param action
     */
    protected abstract TradeResult postReceive(long alter, long oldNumber, long current, Action action);

}
