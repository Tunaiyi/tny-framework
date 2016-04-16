package com.tny.game.base.item.behavior;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Probability;

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

}
