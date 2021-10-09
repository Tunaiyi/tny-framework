package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.probability.*;

import java.util.Map;

/**
 * 奖励
 *
 * @author KGTny
 */
public interface Award extends Probability {

    /**
     * 获取奖励的事物的别名
     *
     * @return
     */
    String getItemAlias(Map<String, Object> attributeMap);

    /**
     * 计算奖励的数量
     *
     * @param attributes 附加参数
     * @return 返回奖励数量
     */
    Number countNumber(ItemModel awardModel, Map<String, Object> attributes);

    /**
     * 获取改变方式
     *
     * @return
     */
    AlterType getAlterType();

    /**
     * 创建TradeItem
     *
     * @param awardModel
     * @param attributeMap
     */
    TradeItem<ItemModel> createTradeItem(boolean valid, ItemModel awardModel, Map<String, Object> attributeMap);

    @Override
    default boolean isEffect(Map<String, Object> attributeMap) {
        return true;
    }
}
