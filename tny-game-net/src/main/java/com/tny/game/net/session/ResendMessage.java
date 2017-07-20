package com.tny.game.net.session;


import com.google.common.collect.Range;
import com.tny.game.net.tunnel.Tunnel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

/**
 * 重发缓存消息的消息
 * <p>
 * Created by Kun Yang on 2017/3/26.
 */
public class ResendMessage<UID> {

    private Range<Integer> resendRange;

    private volatile CompletableFuture<Tunnel<UID>> sendFuture;

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

    private CompletableFuture<Tunnel<UID>> createSentFuture() {
        if (this.sendFuture != null)
            return this.sendFuture;
        synchronized (this) {
            if (this.sendFuture == null)
                this.sendFuture = new CompletableFuture<>();
        }
        return this.sendFuture;
    }

    public CompletionStage<Tunnel<UID>> sendCompletionStage() {
        return createSentFuture();
    }

    public Future<Tunnel<UID>> sendFuture() {
        return createSentFuture();
    }

    public Future<Tunnel<UID>> getSendFuture() {
        return this.sendFuture;
    }

}
