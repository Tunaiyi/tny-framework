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
