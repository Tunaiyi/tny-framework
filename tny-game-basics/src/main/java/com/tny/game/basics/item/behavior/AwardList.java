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

import java.util.*;

/**
 * 奖励列表,记录奖励的所有物品
 *
 * @author KGTny
 */
public interface AwardList {

    Action getAction();

    /**
     * 获取奖励的所有<奖励组<奖励物品的ID - 奖励数量>>
     *
     * @return
     */
    List<AwardDetail> getAwardDetailList();

    List<TradeItem<StuffModel>> getAwardTradeItemList();

    Set<StuffModel> getAwardItemModelSet();

}
