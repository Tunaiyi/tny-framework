package com.tny.game.base.item.behavior;

import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ModelExplorer;

/**
 * 抽象奖励方案
 *
 * @author KGTny
 */
public abstract class AbstractAwardPlan implements AwardPlan {

    public abstract void init(ItemModel itemModel, ItemExplorer itemExplorer, ModelExplorer itemModelExplorer);

}
