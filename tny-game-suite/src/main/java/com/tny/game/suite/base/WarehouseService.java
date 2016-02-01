package com.tny.game.suite.base;

import com.tny.game.base.item.DealedResult;
import com.tny.game.base.item.DoneResult;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.AttributeEntry;

public interface WarehouseService {

    DoneResult<DealedResult> consume(long playerID, Trade result, AttributeEntry<?>... entries);

    DoneResult<DealedResult> consume(long playerID, TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entries);

    DoneResult<DealedResult> receive(long playerID, Trade result, AttributeEntry<?>... entries);

    DoneResult<DealedResult> receive(long playerID, TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entries);

    DoneResult<Boolean> checkTradeBound(long playerID, Trade trade);

}
