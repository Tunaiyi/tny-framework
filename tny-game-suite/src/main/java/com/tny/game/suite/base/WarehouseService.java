package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.common.context.AttrEntry;

public interface WarehouseService {

    void consume(long playerID, Trade result, AttrEntry<?>... entries);

    void consume(long playerID, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries);

    void receive(long playerID, Trade result, AttrEntry<?>... entries);

    void receive(long playerID, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries);

    void deal(long playerID, Trade trade, AttrEntry<?>... entries);

    void deal(long playerID, TryToDoResult result, AttrEntry<?>... entries);

    // DoneResult<Boolean> checkTradeBound(long playerId, Trade trade);

}
