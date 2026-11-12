/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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