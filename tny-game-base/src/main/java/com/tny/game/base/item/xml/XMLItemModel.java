package com.tny.game.base.item.xml;

import com.tny.game.base.item.AbstractItemModel;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.utils.collection.EmptyImmutableList;
import com.tny.game.common.utils.collection.EmptyImmutableMap;
import com.tny.game.common.utils.collection.EmptyImmutableSet;

import java.util.Collections;
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

    @Override
    @SuppressWarnings("unchecked")
    public <IT extends ItemType> IT getItemType() {
        return (IT) itemType();
    }

    protected abstract ItemType itemType();

    @Override
    protected String getCurrentFormula() {
        return this.currentFormula == null ? (DEMAND_ITEM + " == null ? 0 : " + DEMAND_ITEM + ".number") : this.currentFormula;
    }

    protected void init(ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.init)
            return;
        if (this.behaviorPlanList == null)
            this.behaviorPlanList = new EmptyImmutableList<>();
        if (this.behaviorPlanMap == null)
            this.behaviorPlanMap = new EmptyImmutableMap<>();

        if (this.attrAliasSet == null)
            this.attrAliasSet = new EmptyImmutableSet<>();

        if (this.abilityMap == null)
            this.abilityMap = new EmptyImmutableMap<>();

        if (this.actionBehaviorPlanMap == null)
            this.actionBehaviorPlanMap = new EmptyImmutableMap<>();

        this.attrAliasSet = Collections.unmodifiableSet(this.attrAliasSet);
        this.behaviorPlanList = Collections.unmodifiableList(this.behaviorPlanList);
        this.abilityMap = Collections.unmodifiableMap(this.abilityMap);

        for (XMLBehaviorPlan behaviorPlan : this.behaviorPlanList) {
            behaviorPlan.init(this, itemExplorer, itemModelExplorer);
            this.behaviorPlanMap.put(behaviorPlan.getBehavior(), behaviorPlan);
            for (Action action : behaviorPlan.getActionPlanMap().keySet())
                this.actionBehaviorPlanMap.put(action, behaviorPlan);
        }
        this.behaviorPlanMap = Collections.unmodifiableMap(this.behaviorPlanMap);
        this.actionBehaviorPlanMap = Collections.unmodifiableMap(this.actionBehaviorPlanMap);
        this.init = true;
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
    }

}
