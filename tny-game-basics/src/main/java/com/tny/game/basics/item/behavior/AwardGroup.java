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
import com.tny.game.basics.item.probability.*;

import java.util.*;

/**
 * 奖励物品组
 *
 * @author KGTny
 */
public interface AwardGroup extends Probability, ProbabilityGroup<Award> {

    /**
     * 获取奖励组所奖励的所有物品ID列表
     *
     * @return
     */
    Set<StuffModel> getAwardSet(Map<String, Object> attributeMap);

    /**
     * 计算该组奖励物品和数量
     *
     * @param attributeMap 计算参数
     * @return 返回奖励的物品和数量
     */
    List<TradeItem<StuffModel>> countAwardNumber(boolean merge, Map<String, Object> attributeMap);

    /**
     * 创建奖励结果集
     *
     * @param playerId     玩家id
     * @param action       奖励的操作类型
     * @param attributeMap 附加参数
     * @return 返回结果集
     */
    List<TradeItem<StuffModel>> countAwardResult(long playerId, Action action, boolean merge, Map<String, Object> attributeMap);

}
