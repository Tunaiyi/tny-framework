package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;

import java.util.Map;

import static com.tny.game.base.item.ItemsImportKey.$PARAMS;


/**
 * 奖励对象
 *
 * @author KGTny
 */
public abstract class AbstractAward extends DemandParamsObject implements Award {

    public abstract void init();

    @Override
    public TradeItem<ItemModel> createTradeItem(boolean valid, ItemModel awardModel, Map<String, Object> attributeMap) {
        AlterType type = this.getAlterType();
        Map<DemandParam, Object> paramMap = this.countAndSetDemandParams($PARAMS, attributeMap);
        Number number = this.countNumber(awardModel, attributeMap);
        if (number.doubleValue() > 0.0) {
            return new SimpleTradeItem<>(awardModel, number, type == null ? AlterType.IGNORE : type, valid, paramMap);
        } else {
            return null;
        }
    }

}
