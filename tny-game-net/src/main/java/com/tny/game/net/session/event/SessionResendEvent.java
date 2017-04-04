package com.tny.game.net.session.event;

import com.google.common.collect.Range;
import com.tny.game.net.tunnel.Tunnel;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionResendEvent<UID> extends BaseSessionEvent implements SessionOutputEvent {

    private Range<Integer> resendRange;

    public SessionResendEvent(Tunnel<UID> tunnel, Range<Integer> resendRange) {
        super(tunnel);
        this.resendRange = resendRange;
    }

    public Range<Integer> getResendRange() {
        return resendRange;
    }

    @Override
    public SessionEventType getEventType() {
        return SessionEventType.RESEND;
    }

}
