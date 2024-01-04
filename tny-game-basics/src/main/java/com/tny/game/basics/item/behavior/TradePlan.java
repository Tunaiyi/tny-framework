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

import com.tny.game.basics.item.*;

import java.util.Map;

/**
 * 交易(扣除/奖励)方案接口
 *
 * @author KGTny
 */
public interface TradePlan {

    /**
     * 创建交易结果集
     *
     * @param playerId     玩家ID
     * @param action       行为
     * @param attributeMap 计算参数
     * @return 返回交易结果集
     */
    Trade createTrade(long playerId, Action action, Map<String, Object> attributeMap);

    /**
     * 交易的方式
     *
     * @return 返回交易方式
     */
    //	public AlertType getAlertType();

}
