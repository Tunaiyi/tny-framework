package com.tny.game.net.dispatcher;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class MessageOrder<E> {

    private E message;

    private MessageOrderType orderType;

    private MessageSendFuture sendFuture;

    private MessageFuture<?> messageFuture;

    public MessageOrder(E message, MessageOrderType orderType) {
        this.message = message;
        this.orderType = orderType;
    }
}
