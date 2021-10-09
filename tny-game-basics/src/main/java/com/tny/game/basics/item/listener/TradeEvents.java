package com.tny.game.basics.item.listener;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;
import com.tny.game.common.event.bus.*;

/**
 * Created by Kun Yang on 16/2/13.
 */
public interface TradeEvents {

    BindP3EventBus<TradeListener, Warehouse, Action, Trade, Attributes>
            RECEIVE_EVENT = EventBuses.of(TradeListener.class,
            TradeListener::handleReceive);

    BindP3EventBus<TradeListener, Warehouse, Action, Trade, Attributes>
            CONSUME_EVENT = EventBuses.of(TradeListener.class,
            TradeListener::handleConsume);

}
