package com.tny.game.net.session.event;

import com.google.common.collect.Range;
import com.tny.game.net.tunnel.Tunnel;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionResendEvent<UID> extends BaseSessionEvent<UID> implements SessionOutputEvent<UID> {

    private Range<Integer> resendRange;

    private CompletableFuture<Tunnel<UID>> sendFuture;

    public SessionResendEvent(Tunnel<UID> tunnel, Range<Integer> resendRange, CompletableFuture<Tunnel<UID>> sendFuture) {
        super(tunnel);
        this.resendRange = resendRange;
        this.sendFuture = sendFuture;
    }

    public Range<Integer> getResendRange() {
        return resendRange;
    }

    public boolean hasResendFuture() {
        return sendFuture != null;
    }

    public void cancelResendWait(boolean mayInterruptIfRunning) {
        CompletableFuture<Tunnel<UID>> sendFuture = this.sendFuture;
        if (sendFuture != null && !sendFuture.isDone()) {
            sendFuture.cancel(mayInterruptIfRunning);
            this.sendFuture = null;
        }
    }

    public void resendSuccess(Tunnel<UID> tunnel) {
        CompletableFuture<Tunnel<UID>> sendFuture = this.sendFuture;
        if (sendFuture != null && !sendFuture.isDone()) {
            sendFuture.complete(tunnel);
            this.sendFuture = null;
        }
    }

    public void resendFailed(Throwable cause) {
        CompletableFuture<Tunnel<UID>> sendFuture = this.sendFuture;
        if (sendFuture != null && !sendFuture.isDone()) {
            sendFuture.completeExceptionally(cause);
            this.sendFuture = null;
        }
    }


    @Override
    public SessionEventType getEventType() {
        return SessionEventType.RESEND;
    }

}
