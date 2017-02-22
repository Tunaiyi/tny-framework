package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Message;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class MessageCapsule implements MessageOrder {

    private Message message;

    private MessageSendFuture sendFuture;

    private MessageFuture<?> messageFuture;

    public MessageCapsule(Message message) {
    }

    @Override
    public MessageOrderType getOrderType() {
        return MessageOrderType.SEND;
    }
}
