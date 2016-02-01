package com.tny.game.base.item.behavior.simple;

import com.tny.game.base.item.Trade;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.DemandResult;
import com.tny.game.base.item.behavior.TryToDoResult;

public class SimpleTryToDoResult implements TryToDoResult {

    private final Action action;

    private final DemandResult unsatisfyDemandResult;

    private final Trade award;

    private final Trade cost;

    public SimpleTryToDoResult(Action action, Trade award, Trade cost) {
        this.unsatisfyDemandResult = null;
        this.action = action;
        this.award = award;
        this.cost = cost;
    }

    public SimpleTryToDoResult(Action action, DemandResult unsatisfyDemandResult) {
        this.unsatisfyDemandResult = unsatisfyDemandResult;
        this.award = null;
        this.cost = null;
        this.action = action;
    }

    public DemandResult getUnsatisfyDemandResult() {
        return unsatisfyDemandResult;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public boolean isSatisfy() {
        return unsatisfyDemandResult == null;
    }

    @Override
    public DemandResult getUnsatisfyResult() {
        return unsatisfyDemandResult;
    }

    @Override
    public Trade getAwardTrade() {
        return award;
    }

    @Override
    public Trade getCostTrade() {
        return cost;
    }

}
