package com.tny.game.actor.local.message;

public abstract class BaseAutoReceivedMessage implements AutoReceivedMessage {

    protected AutoReceivedMessageType messageType;

    protected BaseAutoReceivedMessage(AutoReceivedMessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public AutoReceivedMessageType getMessageType() {
        return messageType;
    }

}
