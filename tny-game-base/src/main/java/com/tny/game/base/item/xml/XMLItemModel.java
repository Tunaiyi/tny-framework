package com.tny.game.base.item.xml;

import com.tny.game.base.item.Ability;
import com.tny.game.base.item.AbstractItemModel;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModelExplorer;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.item.behavior.BehaviorPlan;
import com.tny.game.common.formula.FormulaHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    protected void init(ItemExplorer itemExplorer, ItemModelExplorer itemModelExplorer) {
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.init)
            return;
        if (this.behaviorPlanList == null)
            this.behaviorPlanList = new ArrayList<XMLBehaviorPlan>(0);
        if (this.behaviorPlanMap == null)
            this.behaviorPlanMap = new HashMap<Behavior, BehaviorPlan>();

        if (this.attrAliasSet == null)
            this.attrAliasSet = new HashSet<String>(0);

        if (this.abilityMap == null)
            this.abilityMap = new HashMap<Ability, FormulaHolder>(1);

        if (this.actionBehaviorPlanMap == null)
            this.actionBehaviorPlanMap = new HashMap<Action, BehaviorPlan>();

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
