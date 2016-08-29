package com.tny.game.net.kafka;

import com.tny.game.net.base.Message;
import com.tny.game.net.base.MessageType;

/**
 * Created by Kun Yang on 16/8/25.
 */
public enum SessionModel {

    SERVER(MessageType.RESPONSE),

    CLIENT(MessageType.REQUEST),

    //
    ;

    private MessageType messageType;

    SessionModel(MessageType messageType) {
        this.messageType = messageType;
    }

    public boolean isCanSend(Message message) {
        return this.messageType == message.getMessage();
    }

}
