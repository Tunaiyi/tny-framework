package com.tny.game.net.transport;

import com.tny.game.net.transport.message.Message;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class MessageSendEvent<UID> extends BaseMessageEvent<UID> implements MessageOutputEvent<UID>, WriteCallback<UID> {

    private MessageContext<UID> messageContext;

    public MessageSendEvent(NetTunnel<UID> tunnel, MessageContext<UID> messageContext) {
        super(tunnel);
        this.messageContext = messageContext;
    }

    public boolean isHasFuture() {
        return messageContext.isHasSendFuture() && messageContext.isHasMessageFuture();
    }

    public void sendComplete(Message<UID> message) {
        this.messageContext.sendSuccess(this.tunnel, message);
    }

    public void sendFail(Throwable cause) {
        this.messageContext.sendFailed(cause);
    }

    public MessageContext<UID> getMessageContext() {
        return this.messageContext;
    }

    @Override
    public void onWrite(Message<UID> message, boolean success, Throwable cause) {
        if (success)
            this.sendComplete(message);
        else
            this.sendFail(cause);
    }
}
