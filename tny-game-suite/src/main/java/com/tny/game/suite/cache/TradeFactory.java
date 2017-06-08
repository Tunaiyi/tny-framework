package com.tny.game.suite.cache;

import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;

import java.util.List;

/**
 * Created by Kun Yang on 2017/6/5.
 */
@FunctionalInterface
public interface TradeFactory<T extends Trade> {

    T create(Action action, TradeType tradeType, List<? extends TradeItem<?>> tradeItemList);

}
