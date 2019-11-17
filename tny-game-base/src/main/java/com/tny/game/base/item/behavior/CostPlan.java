package com.tny.game.base.item.behavior;

import java.util.*;

/**
 * 消耗方案
 *
 * @author KGTny
 */
public interface CostPlan extends TradePlan {

    /**
     * 获取消耗方案的条件结果
     *
     * @param attributeMap
     * @return
     */
    List<DemandResult> countDemandResultList(long playerId, Map<String, Object> attributeMap);

    /**
     * 尝试做某事
     *
     * @param playerId
     * @param attributes
     * @return
     */
    DemandResultCollector tryToDo(long playerId, boolean tryAll, DemandResultCollector collector, Map<String, Object> attributes);

    /**
     * 获取消耗列表
     *
     * @param attributeMap 计算参数
     * @return 返回奖励列表
     */
    CostList getCostList(long playerId, Action action, Map<String, Object> attributeMap);
}