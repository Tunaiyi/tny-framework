package com.tny.game.base.item.listener;

import com.tny.game.base.item.DealedResult;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.Warehouse;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;
import com.tny.game.event.BindP4EventBus;
import com.tny.game.event.EventBuses;

/**
 * Created by Kun Yang on 16/2/13.
 */
public interface TradeEvents {

    BindP4EventBus<TradeListener, Warehouse, Action, Trade, DealedResult, Attributes>
            RECEIVE_EVENT = EventBuses.of(TradeListener.class,
            TradeListener::handleReceive);

    BindP4EventBus<TradeListener, Warehouse, Action, Trade, DealedResult, Attributes>
            CONSUME_EVENT = EventBuses.of(TradeListener.class,
            TradeListener::handleConsume);

}
