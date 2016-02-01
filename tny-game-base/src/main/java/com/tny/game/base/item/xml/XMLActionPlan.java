package com.tny.game.base.item.xml;

import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemModelExplorer;
import com.tny.game.base.item.behavior.AbstractActionPlan;
import com.tny.game.base.item.behavior.Option;
import com.tny.game.common.formula.FormulaHolder;

import java.util.HashMap;

/**
 * xml映射操作方案
 *
 * @author KGTny
 */
public class XMLActionPlan extends AbstractActionPlan {

    @Override
    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ItemModelExplorer itemModelExplorer) {
        super.init(itemModel, itemExplorer, itemModelExplorer);
        if (optionMap == null)
            optionMap = new HashMap<Option, FormulaHolder>();
        if (this.awardPlan != null)
            this.awardPlan.init(itemModel, itemExplorer, itemModelExplorer);
        if (this.costPlan != null)
            this.costPlan.init(itemModel, itemExplorer, itemModelExplorer);
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
    }

}
