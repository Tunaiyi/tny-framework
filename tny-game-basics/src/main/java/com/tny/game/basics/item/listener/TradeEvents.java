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
