package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;

import java.util.Optional;

/**
 * Created by Kun Yang on 16/9/4.
 */
public class ActionTrades {

    private Action action;

    private Trade awardTrade;

    private Trade costTrade;

    public ActionTrades(Action action) {
        this.action = action;
    }

    public ActionTrades(Action action, Trade awardTrade, Trade costTrade) {
        this.action = action;
        if (awardTrade != null && !awardTrade.isEmpty())
            this.awardTrade = awardTrade;
        if (costTrade != null && !costTrade.isEmpty())
            this.costTrade = costTrade;
    }

    public Action getAction() {
        return action;
    }

    public Optional<Trade> getAwardTrade() {
        return Optional.ofNullable(awardTrade);
    }

    public Optional<Trade> getCostTrade() {
        return Optional.ofNullable(costTrade);
    }
}
