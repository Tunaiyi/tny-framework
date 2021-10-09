package com.tny.game.suite.cache;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.List;

/**
 * Created by Kun Yang on 2017/6/5.
 */
@FunctionalInterface
public interface TradeFactory<T extends Trade> {

    T create(Action action, TradeType tradeType, List<? extends TradeItem<?>> tradeItemList);

}
