package com.tny.game.basics.item.xml;

import com.google.common.collect.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

/**
 * xml映射行为方案对象
 *
 * @author KGTny
 */
public class XMLBehaviorPlan extends AbstractBehaviorPlan {

    /**
     * 行为方案列表,作为映射用
     */
    protected List<AbstractActionPlan> actionPlanList;

    @Override
    public void doInit(ItemModel itemModel, ItemModelContext context) {
        if (this.actionPlanList == null)
            this.actionPlanList = ImmutableList.of();
        if (this.actionPlanMap == null)
            this.actionPlanMap = ImmutableMap.of();

        Map<Action, ActionPlan> actionPlanMap = new HashMap<>();
        for (AbstractActionPlan actionPlan : actionPlanList) {
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
