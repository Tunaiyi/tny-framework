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

import com.tny.game.basics.item.behavior.*;

import java.util.*;

/**
 * 行为结果集
 *
 * @author KGTny
 */
public class SimpleBehaviorResult implements BehaviorResult {

    private List<DemandResult> behaviorResult;

    private Map<Action, ActionResult> actionResultMap;

    public SimpleBehaviorResult(List<DemandResult> behaviorResult, Map<Action, ActionResult> actionResultMap) {
        super();
        this.behaviorResult = Collections.unmodifiableList(behaviorResult);
        this.actionResultMap = Collections.unmodifiableMap(actionResultMap);
    }

    @Override
    public List<DemandResult> getBehaviorDemandResultList() {
        return behaviorResult;
    }

    @Override
    public Map<Action, ActionResult> getActionResultMap() {
        return actionResultMap;
    }

}
