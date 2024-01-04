/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.behavior;

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