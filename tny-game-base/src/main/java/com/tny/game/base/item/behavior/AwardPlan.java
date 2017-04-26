package com.tny.game.base.item.behavior;

import com.tny.game.base.item.probability.ProbabilityGroup;

import java.util.Map;

/**
 * 奖励方案
 *
 * @author KGTny
 */
public interface AwardPlan extends TradePlan, ProbabilityGroup<AwardGroup> {

    /**
     * 获取奖励列表
     *
     * @param playerID     玩家ID
     * @param action       行为
     * @param attributeMap 计算参数
     * @return 返回奖励列表
     */
    AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap);

}
