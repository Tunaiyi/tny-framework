/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
