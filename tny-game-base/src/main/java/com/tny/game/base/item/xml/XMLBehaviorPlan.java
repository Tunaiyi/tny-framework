package com.tny.game.base.item.xml;

import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemModelExplorer;
import com.tny.game.base.item.behavior.AbstractActionPlan;
import com.tny.game.base.item.behavior.AbstractBehaviorPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ItemModelExplorer itemModelExplorer) {
        super.init(itemModel, itemExplorer, itemModelExplorer);
        if (actionPlanList == null)
            actionPlanList = new ArrayList<>(0);
        if (actionPlanMap == null)
            actionPlanMap = new HashMap<>(0);

        for (AbstractActionPlan actionPlan : actionPlanList) {
            actionPlan.init(itemModel, itemExplorer, itemModelExplorer);
            actionPlanMap.put(actionPlan.getAction(), actionPlan);
        }
        actionPlanList = Collections.unmodifiableList(actionPlanList);
        actionPlanMap = Collections.unmodifiableMap(actionPlanMap);
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
    }

}
