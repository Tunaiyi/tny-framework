package com.tny.game.net.session;

import com.google.common.collect.Range;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.*;

import java.util.List;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionResendEvent<UID> extends BaseSessionEvent<UID> implements SessionOutputEvent<UID> {

    private ResendMessage<UID> message;

    public SessionResendEvent(NetTunnel<UID> tunnel, ResendMessage<UID> message) {
        super(tunnel);
        this.message = message;
    }

    @Override
    public SessionEventType getEventType() {
        return SessionEventType.RESEND;
    }

    public Range<Integer> getResendRange() {
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
