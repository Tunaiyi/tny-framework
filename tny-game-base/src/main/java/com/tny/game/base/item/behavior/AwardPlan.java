package com.tny.game.base.item.behavior;

import java.util.Map;

/**
 * 奖励方案
 *
 * @author KGTny
 */
public interface AwardPlan extends TradePlan {

    /**
     * 获取奖励列表
     *
     * @param atrributeMap 计算参数
     * @return 返回奖励列表
     */
    public AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap);

}
