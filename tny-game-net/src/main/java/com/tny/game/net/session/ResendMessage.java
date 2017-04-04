package com.tny.game.net.session;


import com.google.common.collect.Range;

/**
 * 重发缓存消息的消息
 * <p>
 * Created by Kun Yang on 2017/3/26.
 */
public class ResendMessage {

    private Range<Integer> resendRange;

    public static ResendMessage of(int messageID) {
        return new ResendMessage(Range.singleton(messageID));
    }

    public static ResendMessage from(int fromMessageID) {
        return new ResendMessage(Range.atLeast(fromMessageID));
    }

    public static ResendMessage fromTo(int fromMessageID, int toMessageID) {
        return new ResendMessage(Range.closed(fromMessageID, toMessageID));
    }

    public static ResendMessage ofRange(Range<Integer> resendRange) {
        return new ResendMessage(resendRange);
    }

    private ResendMessage(Range<Integer> resendRange) {
        this.resendRange = resendRange;
    }

    public Range<Integer> getResendRange() {
        return resendRange;
    }

}
