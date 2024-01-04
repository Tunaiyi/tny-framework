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

package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;
import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/17 2:20 下午
 */
public interface TradeService {

    void deal(long playerId, Trade trade, AttrEntry<?>... entries);

    void deal(long playerId, TryToDoResult result, AttrEntry<?>... entries);

    ResultCode checkBound(long playerId, Trade trade);

}
