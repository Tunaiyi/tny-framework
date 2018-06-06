package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;

/**
 * 抽象奖励方案
 *
 * @author KGTny
 */
public abstract class AbstractAwardPlan  extends DemandParamsObject implements AwardPlan {

    public abstract void init(ItemModel itemModel, ItemModelContext context);

}
