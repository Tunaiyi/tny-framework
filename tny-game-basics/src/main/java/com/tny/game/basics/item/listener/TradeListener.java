package com.tny.game.basics.item.listener;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

public interface TradeListener {

    void handleReceive(Warehouse source, Action action, Trade trade, Attributes attributes);

    void handleConsume(Warehouse source, Action action, Trade trade, Attributes attributes);

}
