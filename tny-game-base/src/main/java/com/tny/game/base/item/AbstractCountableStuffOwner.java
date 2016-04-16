package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;

/**
 * 抽象物品管理器
 *
 * @param <SM>
 * @param <S>
 * @author KGTny
 */
public abstract class AbstractCountableStuffOwner<SM extends CountableStuffModel, S extends BaseCountableStuff<SM, ?>> extends StuffOwner<SM, S> {

    protected void doConsume(S stuff, Action action, TradeItem<SM> tradeItem, Attributes attributes) {
        stuff.consume(action, tradeItem, attributes);
    }

    protected void doReceive(S stuff, Action action, TradeItem<SM> tradeItem, Attributes attributes) {
        stuff.receive(action, tradeItem, attributes);
    }

    /**
     * 构建item <br>
     *
     * @param oim
     * @return
     */
    protected abstract <OIM extends ItemModel> S createStuff(OIM oim);

}
