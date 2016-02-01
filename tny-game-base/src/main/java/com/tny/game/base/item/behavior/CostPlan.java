package com.tny.game.base.item.behavior;

import java.util.List;
import java.util.Map;

/**
 * 消耗方案
 *
 * @author KGTny
 */
public interface CostPlan extends TradePlan {

    /**
     * 获取消耗方案的条件结果
     *
     * @param atrributeMap
     * @return
     */
    public List<DemandResult> countDemandResultList(long playerID, Map<String, Object> atrributeMap);

    /**
     * 尝试做某事
     *
     * @param playerID
     * @param attributes
     * @return
     */
    public DemandResult tryToDo(long playerID, Map<String, Object> attributes);

    /**
     * 获取消耗列表
     *
     * @param atrributeMap 计算参数
     * @return 返回奖励列表
     */
    public CostList getCostList(long playerID, Action action, Map<String, Object> attributeMap);
}