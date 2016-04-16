package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;

public abstract class LongCountableStuff<SM extends CountableStuffModel<Long>> extends BaseCountableStuff<SM, Long> {

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
    protected TradeResult consume(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
        long alter = this.getConsumeAlterType(tradeItem).consume(this, tradeItem.getNumber()).longValue();
        if (alter > 0) {
            long oldNumber = this.getNumber();
            this.number -= alter;
            return this.postConsume(alter, oldNumber, this.number, action, attributes);
        }
        return TradeResult.empty();
    }

    @Override
    protected TradeResult receive(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
        long alter = this.getReceiveAlterType(tradeItem).receive(this, tradeItem.getNumber()).longValue();
        if (alter > 0) {
            long oldNumber = this.getNumber();
            this.number += alter;
            return this.postReceive(alter, oldNumber, this.number, action, attributes);
        }
        return TradeResult.empty();
    }

    @Override
    protected void setNumber(Long number) {
        this.number = number;
    }

}
