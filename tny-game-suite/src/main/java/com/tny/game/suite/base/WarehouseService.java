package com.tny.game.suite.base;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

public interface WarehouseService {

    void consume(long playerId, Trade result, AttrEntry<?>... entries);

    void consume(long playerId, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries);

    void receive(long playerId, Trade result, AttrEntry<?>... entries);

    void receive(long playerId, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries);

    void deal(long playerId, Trade trade, AttrEntry<?>... entries);

    void deal(long playerId, TryToDoResult result, AttrEntry<?>... entries);

    // DoneResult<Boolean> checkTradeBound(long playerId, Trade trade);

}
