package com.tny.game.net.transport;

import com.google.common.collect.Range;
import com.tny.game.net.transport.message.Message;

import java.util.List;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class MessageResendEvent<UID> extends BaseMessageEvent<UID> implements MessageOutputEvent<UID> {

    private ResendMessage<UID> message;

    public MessageResendEvent(NetTunnel<UID> tunnel, ResendMessage<UID> message) {
        super(tunnel);
        this.message = message;
    }

    public Range<Long> getResendRange() {
        return message.getResendRange();
    }

    public boolean isHasFuture() {
        return message.isHasFuture();
    }

    public void resendSuccess(Tunnel<UID> tunnel, List<Message<UID>> messages) {
        message.resendSuccess(tunnel, messages);
    }

    public void resendFailed(Throwable cause) {
        message.resendFailed(cause);
    }

}
