package com.tny.game.base.item.xml;

import com.google.common.collect.ImmutableSet;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.behavior.AbstractActionPlan;

import java.util.HashMap;

/**
 * xml映射操作方案
 *
 * @author KGTny
 */
public class XMLActionPlan extends AbstractActionPlan {

    @Override
    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        super.init(itemModel, itemExplorer, itemModelExplorer);
        if (optionMap == null)
            optionMap = new HashMap<>();
        if (action != null) {
            actions = ImmutableSet.of(action);
        } else if (actions != null) {
            actions = ImmutableSet.copyOf(actions);
        }
        if (this.awardPlan != null)
            this.awardPlan.init(itemModel, itemExplorer, itemModelExplorer);
        if (this.costPlan != null)
            this.costPlan.init(itemModel, itemExplorer, itemModelExplorer);
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
    }

}
