package com.tny.game.basics.item.listener;

import com.tny.game.basics.item.*;
import com.tny.game.common.context.*;

public interface TradeListener {

	void handleReward(Warehouse warehouse, Trade trade, Attributes attributes);

	void handleConsume(Warehouse warehouse, Trade trade, Attributes attributes);

}
