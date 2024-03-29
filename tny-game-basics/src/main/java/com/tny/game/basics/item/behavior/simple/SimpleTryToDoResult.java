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

package com.tny.game.basics.item.behavior.simple;

import com.google.common.collect.ImmutableList;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

public class SimpleTryToDoResult implements TryToDoResult {

    private final Action action;

    private List<DemandResult> failDemandResults = ImmutableList.of();

    private final Trade award;

    private final Trade cost;

    public SimpleTryToDoResult(Action action, Trade award, Trade cost) {
        this.action = action;
        this.award = award;
        this.cost = cost;
        this.failDemandResults = ImmutableList.of();
    }

    public SimpleTryToDoResult(Action action, DemandResult failDemandResults) {
        this.award = null;
        this.cost = null;
        this.action = action;
        this.failDemandResults = ImmutableList.of(failDemandResults);
    }

    public SimpleTryToDoResult(Action action, Collection<DemandResult> failDemandResults) {
        this.award = null;
        this.cost = null;
        this.action = action;
        this.failDemandResults = ImmutableList.copyOf(failDemandResults);
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public boolean isSatisfy() {
        return failDemandResults == null || failDemandResults.isEmpty();
    }

    @Override
    public DemandResult getFailResult() {
        if (failDemandResults.isEmpty()) {
            return null;
        }
        return failDemandResults.get(0);
    }

    @Override
    public List<DemandResult> getAllFailResults() {
        return failDemandResults;
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
