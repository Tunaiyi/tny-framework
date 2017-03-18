package com.tny.game.net.session.event;

import com.google.common.collect.Range;
import com.tny.game.net.message.Message;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionResendOrder implements SessionOutputEvent {

    private Range<Integer> resendRange;

    public SessionResendOrder(Range<Integer> resendRange) {
        this.resendRange = resendRange;
    }

    @Override
    public Message<?> getMessage() {
        return null;
    }

    public Range<Integer> getResendRange() {
        return resendRange;
    }

    @Override
    public SessionEventType getEventType() {
        return SessionEventType.RESEND;
    }
}
