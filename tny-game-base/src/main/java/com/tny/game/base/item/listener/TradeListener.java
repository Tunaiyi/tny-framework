package com.tny.game.base.item.listener;

import com.tny.game.base.item.DealedResult;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.Warehouse;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;

public interface TradeListener {

    void handleReceive(Warehouse source, Action action, Trade trade, DealedResult dealedResult, Attributes attributes);

    void handleConsume(Warehouse source, Action action, Trade trade, DealedResult dealedResult, Attributes attributes);

}
