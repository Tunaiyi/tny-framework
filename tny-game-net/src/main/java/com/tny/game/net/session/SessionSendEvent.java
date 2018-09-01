package com.tny.game.net.session;

import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.*;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionSendEvent<UID> extends BaseSessionEvent<UID> implements SessionOutputEvent<UID>, WriteCallback<UID> {

    private MessageContent<UID> content;

    public SessionSendEvent(NetTunnel<UID> tunnel, MessageContent<UID> content) {
        super(tunnel);
        this.content = content;
    }

    @Override
    public SessionEventType getEventType() {
        return SessionEventType.MESSAGE;
    }

    public boolean isHasFuture() {
        return content.isHasSendFuture() && content.isHasMessageFuture();
    }

    public void sendComplete(Message<UID> message) {
        this.content.sendSuccess(message);
    }

    public void sendFail(Throwable cause) {
        this.content.sendFailed(cause);
    }

    public MessageContent<UID> getContent() {
        return this.content;
    }

    @Override
    public void onWrite(Message<UID> message, boolean success, Throwable cause) {
        if (success)
            this.sendComplete(message);
        else
            this.sendFail(cause);
    }
}
