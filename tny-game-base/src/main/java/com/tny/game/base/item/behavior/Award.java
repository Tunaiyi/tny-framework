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
    public String getItemAlias(Map<String, Object> atrributeMap);

    /**
     * 计算奖励的数量
     *
     * @param attributes 附加参数
     * @return 返回奖励数量
     */
    public int countNumber(ItemModel awardModel, Map<String, Object> attributes);

    /**
     * 获取改变方式
     *
     * @return
     */
    public AlterType getAlterType();

    /**
     * 计算附加参数
     *
     * @param attributeMap
     * @return
     */
    public Map<DemandParam, Object> countDemandParam(Map<String, Object> attributeMap);

}
