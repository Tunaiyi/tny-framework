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

package com.tny.game.basics.item.listener;

import com.tny.game.basics.item.*;
import com.tny.game.common.context.*;
import com.tny.game.common.event.*;

/**
 * Created by Kun Yang on 16/2/13.
 */
public interface TradeEvents {

    A2BindEvent<TradeListener, Warehouse, Trade, Attributes>
            REWARD_EVENT = Events.ofEvent(TradeListener.class,
            TradeListener::handleReward);

    A2BindEvent<TradeListener, Warehouse, Trade, Attributes>
            CONSUME_EVENT = Events.ofEvent(TradeListener.class,
            TradeListener::handleConsume);

}
