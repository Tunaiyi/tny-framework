package com.tny.game.basics.item.behavior;

import java.util.*;

/**
 * Created by Kun Yang on 2017/6/26.
 */
public class DemandResultCollector {

    private List<DemandResult> failedDemands = new ArrayList<>();

    private List<CostDemandResult> costDemands = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public void addDemandResult(DemandResult result) {
        if (!result.isSatisfy()) {
            failedDemands.add(result);
        } else if (result instanceof CostDemandResult) {
            costDemands.add((CostDemandResult) result);
        }
    }

    @SuppressWarnings("unchecked")
    public void addDemandResult(Collection<DemandResult> results) {
        results.forEach(this::addDemandResult);
    }

    public boolean isFailed() {
        return !this.failedDemands.isEmpty();
    }

    public List<DemandResult> getFailedDemands() {
        return failedDemands;
    }

    public List<CostDemandResult> getCostDemands() {
        return costDemands;
    }
}