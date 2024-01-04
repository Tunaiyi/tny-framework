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

import com.tny.game.basics.item.probability.*;

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
     * @param playerId     玩家ID
     * @param action       行为
     * @param attributeMap 计算参数
     * @return 返回奖励列表
     */
    AwardList getAwardList(long playerId, Action action, Map<String, Object> attributeMap);

}
