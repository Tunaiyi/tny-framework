package com.tny.game.net.dispatcher;

import com.google.common.collect.Range;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class MessageResendOrder implements MessageOrder {

    private Range<Integer> resendRange;

    public MessageResendOrder(Range<Integer> resendRange) {
        this.resendRange = resendRange;
    }

    @Override
    public MessageOrderType getOrderType() {
        return MessageOrderType.RESEND;
    }
}
