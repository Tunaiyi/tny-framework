package com.tny.game.base.item;

import com.tny.game.base.item.listener.TradeEvents;
import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;

import java.util.Map;

public class BaseAttributeKey {

    public static final AttrKey<Map<Object, Object>> TRADE_EVENT_CONTEXTMAP = AttrKeys.key(TradeEvents.class, "TRADE_EVENT_CONTEXTMAP");

}
