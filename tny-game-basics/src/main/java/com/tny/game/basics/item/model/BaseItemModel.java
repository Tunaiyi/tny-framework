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

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.collection.empty.*;

import java.util.List;

/**
 * 抽象xml映射事物模型
 *
 * @author KGTny
 */
public abstract class BaseItemModel extends AbstractItemModel {

    /**
     * 行为列表
     */
    protected List<BaseBehaviorPlan> behaviorPlanList;

    protected String currentFormula;

    protected String demandFormula;

    @Override
    @SuppressWarnings("unchecked")
    public ItemType getItemType() {
        return itemType();
    }

    protected abstract ItemType itemType();

    @Override
    protected String getCurrentFormula() {
        return this.currentFormula == null ? super.getCurrentFormula() : this.currentFormula;
    }

    @Override
    protected String getDemandFormula() {
        return this.demandFormula == null ? super.getDemandFormula() : this.demandFormula;
    }

    @Override
    protected void onItemInit(ItemModelContext context) {
        if (this.behaviorPlanList == null) {
            this.behaviorPlanList = new EmptyImmutableList<>();
        }
        if (this.actionBehaviorPlanMap == null) {
            this.actionBehaviorPlanMap = new EmptyImmutableMap<>();
        }
        for (BaseBehaviorPlan behaviorPlan : this.behaviorPlanList) {
            behaviorPlan.init(this, context);
            this.behaviorPlanMap.put(behaviorPlan.getBehavior(), behaviorPlan);
            for (Action action : behaviorPlan.getActionPlanMap().keySet())
                this.actionBehaviorPlanMap.put(action, behaviorPlan);
        }
    }

}
