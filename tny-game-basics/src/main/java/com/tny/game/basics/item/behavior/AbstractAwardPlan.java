package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;

/**
 * 抽象奖励方案
 *
 * @author KGTny
 */
public abstract class AbstractAwardPlan extends DemandParamsObject implements AwardPlan {

    public abstract void init(ItemModel itemModel, ItemModelContext context);

}
