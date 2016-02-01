package com.tny.game.base.item.listener;

import com.tny.game.base.item.DealedResult;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.Warehouse;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.AttributeEntry;
import com.tny.game.event.Event;

public class TradeEvent extends Event<Warehouse> {

    public static enum TradeEventType {

        RECEVIE("receive"),

        CONSUME("consume");

        private final String HANDLE_NAME;

        private TradeEventType(String hANDLE_NAME) {
            HANDLE_NAME = hANDLE_NAME;
        }

    }

    private Action action;

    private Trade trade;

    private DealedResult dealedResult;

    private TradeEvent(Warehouse soure, Action action, TradeEventType type, Trade trade, DealedResult dealedResult) {
        super(type.HANDLE_NAME, soure);
        this.trade = trade;
        this.action = action;
        this.dealedResult = dealedResult;
    }

    public Trade getTrade() {
        return trade;
    }

    public Action getAction() {
        return action;
    }

    public DealedResult getDealedResult() {
        return dealedResult;
    }

    public static void dispatch(Warehouse soure, Action action, TradeEventType type, Trade trade, DealedResult dealedResult, AttributeEntry<?>... entries) {
        TradeEvent event = new TradeEvent(soure, action, type, trade, dealedResult);
        event.setAttribute(entries);
        event.dispatch();
    }

}
