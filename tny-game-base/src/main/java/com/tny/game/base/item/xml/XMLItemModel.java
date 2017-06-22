package com.tny.game.base.item.xml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tny.game.base.item.AbstractItemModel;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.utils.collection.EmptyImmutableMap;

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
    public <IT extends ItemType> IT getItemType() {
        return (IT) itemType();
    }

    protected abstract ItemType itemType();

    @Override
    protected String getCurrentFormula() {
        return this.currentFormula == null ? super.getCurrentFormula() : this.currentFormula;
    }

    @Override
    protected String getDemandFormula() {
        return this.demandFormula == null ? super.getCurrentFormula() : this.demandFormula;
    }

    protected void init(ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.init)
            return;
        if (this.behaviorPlanList == null)
            this.behaviorPlanList = ImmutableList.of();

        if (this.attrAliasSet == null)
            this.attrAliasSet = ImmutableSet.of();

        if (this.abilityMap == null)
            this.abilityMap = ImmutableMap.of();

        if (this.tags == null)
            this.tags = ImmutableSet.of();

        this.attrAliasSet = ImmutableSet.copyOf(this.attrAliasSet);
        this.behaviorPlanList = ImmutableList.copyOf(this.behaviorPlanList);
        this.abilityMap = ImmutableMap.copyOf(this.abilityMap);
        this.tags = ImmutableSet.copyOf(this.tags);


        if (this.actionBehaviorPlanMap == null)
            this.actionBehaviorPlanMap = new EmptyImmutableMap<>();

        if (this.behaviorPlanMap == null)
            this.behaviorPlanMap = new EmptyImmutableMap<>();

        for (XMLBehaviorPlan behaviorPlan : this.behaviorPlanList) {
            behaviorPlan.init(this, itemExplorer, itemModelExplorer);
            this.behaviorPlanMap.put(behaviorPlan.getBehavior(), behaviorPlan);
            for (Action action : behaviorPlan.getActionPlanMap().keySet())
                this.actionBehaviorPlanMap.put(action, behaviorPlan);
        }
        this.behaviorPlanMap = ImmutableMap.copyOf(this.behaviorPlanMap);
        this.actionBehaviorPlanMap = ImmutableMap.copyOf(this.actionBehaviorPlanMap);
        this.init = true;
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
    }

}
