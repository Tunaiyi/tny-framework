package com.tny.game.net.session.event;

import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageSentHandler;
import com.tny.game.net.session.Session;

import java.rmi.server.UID;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionSendEvent implements SessionOutputEvent {

    private Session<UID> session;

    private SessionEventType eventType;

    private boolean sent;

    private Message message;

    private MessageSentHandler<?> sentHandler;

    public SessionSendEvent(Message message, boolean sent, SessionEventType eventType, MessageSentHandler<?> sentHandler) {
        this.message = message;
        this.sent = sent;
        this.eventType = eventType;
        this.sentHandler = sentHandler;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public SessionEventType getEventType() {
        return eventType;
    }

    public MessageSentHandler<?> getSentHandler() {
        return sentHandler;
    }

}
