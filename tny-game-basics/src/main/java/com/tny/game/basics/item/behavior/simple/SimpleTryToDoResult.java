/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
