package com.tny.game.basics.item.xml;

import com.google.common.collect.ImmutableList;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.List;

/**
 * 抽象xml映射事物模型
 *
 * @author KGTny
 */
public abstract class XMLItemModel extends AbstractItemModel {

    /**
     * 行为列表
     */
    protected List<XMLBehaviorPlan> behaviorPlanList;

    protected boolean init = false;

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

    protected void doInit(ItemModelContext context) {
        if (this.behaviorPlanList == null)
            this.behaviorPlanList = ImmutableList.of();
        this.behaviorPlanList = ImmutableList.copyOf(this.behaviorPlanList);
        for (XMLBehaviorPlan behaviorPlan : this.behaviorPlanList) {
            behaviorPlan.init(this, context);
            this.behaviorPlanMap.put(behaviorPlan.getBehavior(), behaviorPlan);
            for (Action action : behaviorPlan.getActionPlanMap().keySet())
                this.actionBehaviorPlanMap.put(action, behaviorPlan);
        }
    }

    @Override
    protected void init(ItemModelContext context) {
        super.init(context);
    }
}
