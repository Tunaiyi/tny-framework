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

import java.util.Map;

/**
 * 奖励
 *
 * @author KGTny
 */
public interface Award extends Probability {

    /**
     * 获取奖励的事物的别名
     *
     * @return
     */
    String getItemAlias(Map<String, Object> attributeMap);

    /**
     * 计算奖励的数量
     *
     * @param attributes 附加参数
     * @return 返回奖励数量
     */
    Number countNumber(ItemModel awardModel, Map<String, Object> attributes);

    /**
     * 获取改变方式
     *
     * @return
     */
    AlterType getAlterType();

    /**
     * 创建TradeItem
     *
     * @param awardModel
     * @param attributeMap
     */
    TradeItem<StuffModel> createTradeItem(boolean valid, StuffModel awardModel, Map<String, Object> attributeMap);

    @Override
    default boolean isEffect(Map<String, Object> attributeMap) {
        return true;
    }

}
