package com.tny.game.basics.item.listener;

import com.tny.game.basics.item.*;
import com.tny.game.common.context.*;
import com.tny.game.common.event.bus.*;

/**
 * Created by Kun Yang on 16/2/13.
 */
public interface TradeEvents {

    BindP2EventBus<TradeListener, Warehouse, Trade, Attributes>
            REWARD_EVENT = EventBuses.of(TradeListener.class,
            TradeListener::handleReward);

    BindP2EventBus<TradeListener, Warehouse, Trade, Attributes>
            CONSUME_EVENT = EventBuses.of(TradeListener.class,
            TradeListener::handleConsume);

}
