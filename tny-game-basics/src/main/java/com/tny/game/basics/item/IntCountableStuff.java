package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

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
    protected void consume(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
        int alter = this.getConsumeAlterType(tradeItem).consume(this, tradeItem.getNumber()).intValue();
        if (alter > 0) {
            int oldNumber = this.getNumber();
            this.number -= alter;
            this.postConsume(alter, oldNumber, this.number, action, attributes);
        }
    }

    @Override
    protected void receive(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
        int alter = this.getReceiveAlterType(tradeItem).receive(this, tradeItem.getNumber()).intValue();
        if (alter > 0) {
            int oldNumber = this.getNumber();
            this.number += alter;
            this.postReceive(alter, oldNumber, this.number, action, attributes);
        }
    }

    @Override
    protected void setNumber(Integer number) {
        this.number = number;
    }

}
