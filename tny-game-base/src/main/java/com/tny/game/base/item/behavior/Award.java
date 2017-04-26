package com.tny.game.base.item.behavior;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;
import com.tny.game.base.item.probability.Probability;

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
    int countNumber(ItemModel awardModel, Map<String, Object> attributes);

    /**
     * 获取改变方式
     *
     * @return
     */
    AlterType getAlterType();

    /**
     * 计算附加参数
     *
     * @param attributeMap
     * @return
     */
    Map<DemandParam, Object> countDemandParam(Map<String, Object> attributeMap);

    /**
     * 创建TradeItem
     *
     * @param awardModel
     * @param attributeMap
     */
    default TradeItem<ItemModel> createTradeItem(boolean valid, ItemModel awardModel, Map<String, Object> attributeMap) {
        AlterType type = this.getAlterType();
        Map<DemandParam, Object> paramMap = this.countDemandParam(attributeMap);
        int number = this.countNumber(awardModel, attributeMap);
        if (number > 0) {
            return new SimpleTradeItem<>(awardModel, type == null ? AlterType.IGNORE : type, number, valid, paramMap);
        } else {
            return null;
        }
    }

    @Override
    default boolean isEffect(Map<String, Object> attributeMap) {
        return true;
    }
}
