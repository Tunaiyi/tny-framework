package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;

/**
 * 抽象物品管理器
 *
 * @param <SM>
 * @param <S>
 * @author KGTny
 */
public abstract class AbstractCountableStuffOwner<SM extends CountableStuffModel, S extends BaseCountableStuff<SM, ?>> extends StuffOwner<SM, S> {

    protected void doConsume(S stuff, Action action, TradeItem<? extends SM> tradeItem) {
        stuff.consume(action, tradeItem);
    }

    protected void doReceive(S stuff, Action action, TradeItem<? extends SM> tradeItem) {
        stuff.receive(action, tradeItem);
    }

    /**
     * 构建item <br>
     *
     * @param oim
     * @return
     */
    protected abstract <OIM extends ItemModel> S createStuff(OIM oim);

}
