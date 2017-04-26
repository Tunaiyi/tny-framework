package com.tny.game.base.item.behavior;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;

import java.util.Map;

/**
 * 奖励对象
 *
 * @author KGTny
 */
public abstract class AbstractAward implements Award {

    public abstract void init();

    @Override
    public TradeItem<ItemModel> createTradeItem(boolean valid, ItemModel awardModel, Map<String, Object> attributeMap) {
        AlterType type = this.getAlterType();
        Map<DemandParam, Object> paramMap = this.countDemandParam(attributeMap);
        int number = this.countNumber(awardModel, attributeMap);
        if (number > 0) {
            return new SimpleTradeItem<>(awardModel, type == null ? AlterType.IGNORE : type, number, valid, paramMap);
        } else {
            return null;
        }
    }
}
