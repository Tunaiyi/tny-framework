package com.tny.game.base.item;

import com.tny.game.base.item.listener.*;
import com.tny.game.common.context.*;

import java.util.Map;

public class BaseAttributeKey {

    public static final AttrKey<Map<Object, Object>> TRADE_EVENT_CONTEXTMAP = AttrKeys.key(TradeEvents.class, "TRADE_EVENT_CONTEXTMAP");

}
