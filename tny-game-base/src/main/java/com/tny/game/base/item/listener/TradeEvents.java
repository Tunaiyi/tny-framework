package com.tny.game.base.item.listener;

import com.tny.game.base.item.Trade;
import com.tny.game.base.item.Warehouse;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.event.BindP3EventBus;
import com.tny.game.common.event.EventBuses;

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
