package com.tny.game.common.utils;

import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TryToDoResult;
import com.tny.game.common.context.AttrEntry;
import com.tny.game.common.utils.DoneResult;

public interface WarehouseService {

    void consume(long playerID, Trade result, AttrEntry<?>... entries);

    void consume(long playerID, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries);

    void receive(long playerID, Trade result, AttrEntry<?>... entries);

    void receive(long playerID, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries);

    void deal(long playerID, Trade trade, AttrEntry<?>... entries);

    void deal(long playerID, TryToDoResult result, AttrEntry<?>... entries);

    DoneResult<Boolean> checkTradeBound(long playerID, Trade trade);

}
