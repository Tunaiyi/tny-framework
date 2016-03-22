package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;

public abstract class IntCountableStuff<SM extends CountableStuffModel<Integer>> extends BaseCountableStuff<SM, Integer> {

    protected int number;

    @Override
    public Integer getNumberLimit() {
        return this.model.countNumberLimit(this);
    }

    @Override
    public Integer getNumber() {
        return this.number;
    }

    @Override
    protected TradeResult consume(Action action, TradeItem<? extends SM> tradeItem) {
        int alter = this.getConsumeAlterType(tradeItem).consume(this, tradeItem.getNumber()).intValue();
        if (alter > 0) {
            int oldNumber = this.getNumber();
            this.number -= alter;
            return this.postConsume(alter, oldNumber, this.number, action);
        }
        return TradeResult.empty();
    }

    @Override
    protected TradeResult receive(Action action, TradeItem<? extends SM> tradeItem) {
        int alter = this.getReceiveAlterType(tradeItem).receive(this, tradeItem.getNumber()).intValue();
        if (alter > 0) {
            int oldNumber = this.getNumber();
            this.number += alter;
            return this.postReceive(alter, oldNumber, this.number, action);
        }
        return TradeResult.empty();
    }

    @Override
    protected void setNumber(Integer number) {
        this.number = number;
    }

}
