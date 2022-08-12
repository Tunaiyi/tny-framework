/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
