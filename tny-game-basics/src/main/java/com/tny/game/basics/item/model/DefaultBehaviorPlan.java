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

package com.tny.game.basics.item.model;

import com.google.common.collect.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.xml.*;

import java.util.*;

/**
 * xml映射行为方案对象
 *
 * @author KGTny
 */
public class DefaultBehaviorPlan extends BaseBehaviorPlan {

    /**
     * 行为方案列表,作为映射用
     */
    protected List<BaseActionPlan> actionPlanList;

    @Override
    public void doInit(ItemModel itemModel, ItemModelContext context) {
        if (this.actionPlanList == null) {
            this.actionPlanList = ImmutableList.of();
        }
        if (this.actionPlanMap == null) {
            this.actionPlanMap = ImmutableMap.of();
        }

        Map<Action, ActionPlan> actionPlanMap = new HashMap<>();
        for (BaseActionPlan actionPlan : actionPlanList) {
            actionPlan.init(itemModel, context);
            for (Action action : actionPlan.getActions()) {
                actionPlanMap.put(action, actionPlan);
            }
        }
        this.actionPlanList = Collections.unmodifiableList(actionPlanList);
        this.actionPlanMap = Collections.unmodifiableMap(actionPlanMap);
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
    }

}
