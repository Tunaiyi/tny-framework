package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;
import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/17 2:20 下午
 */
public interface TradeService {

    void deal(long playerId, Trade trade, AttrEntry<?>... entries);

    void deal(long playerId, TryToDoResult result, AttrEntry<?>... entries);

    ResultCode checkBound(long playerId, Trade trade);

}
