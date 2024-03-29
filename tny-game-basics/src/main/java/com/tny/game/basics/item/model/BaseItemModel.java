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
