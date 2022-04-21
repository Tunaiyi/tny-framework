package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/25 12:10 下午
 */
public class TradeBuilder {

    private Action action;

    private TradeType tradeType;

    private List<TradeItem<?>> tradeItems = new ArrayList<>();

    public static TradeBuilder awardBuilder(Action action) {
        return new TradeBuilder(action, TradeType.AWARD);
    }

    public static TradeBuilder costBuilder(Action action) {
        return new TradeBuilder(action, TradeType.DEDUCT);
    }

    private TradeBuilder(Action action, TradeType tradeType) {
        this.action = action;
        this.tradeType = tradeType;
    }

    public TradeBuilder addItem(StuffModel itemModel, Number number, AlterType alertType) {
        this.tradeItems.add(new SimpleTradeItem<>(itemModel, number, alertType));
        return this;
    }

    public TradeBuilder addItem(StuffModel itemModel, Number number) {
        this.tradeItems.add(new SimpleTradeItem<>(itemModel, number));
        return this;
    }

    public TradeBuilder addItem(TradeItem<?> item) {
        this.tradeItems.add(item);
        return this;
    }

    public TradeBuilder addItem(TradeItem<?>... items) {
        for (TradeItem<?> item : items)
            this.tradeItems.add(item);
        return this;
    }

    public TradeBuilder addItems(Collection<TradeItem<?>> items) {
        this.tradeItems.addAll(items);
        return this;
    }

    public Trade build() {
        return new SimpleTrade(action, tradeType, this.tradeItems);
    }

}
