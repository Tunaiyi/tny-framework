package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Message;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class MessageCapsule implements MessageOrder {

    private Message message;

    private MessageSentHandler sentHandler;

    private MessageFuture<?> messageFuture;

    public MessageCapsule(Message message, MessageSentHandler sentHandler, MessageFuture<?> messageFuture) {
        this.message = message;
        this.sentHandler = sentHandler;
        this.messageFuture = messageFuture;
    }

    public Message getMessage() {
        return message;
    }

    public MessageSentHandler getSentHandler() {
        return sentHandler;
    }

    public MessageFuture<?> getMessageFuture() {
        return messageFuture;
    }

    @Override
    public MessageOrderType getOrderType() {
        return MessageOrderType.SEND;
    }

}
