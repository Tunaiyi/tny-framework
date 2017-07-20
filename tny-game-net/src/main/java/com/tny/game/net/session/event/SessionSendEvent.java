package com.tny.game.net.session.event;

import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.tunnel.TunnelContent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionSendEvent<UID> extends BaseSessionEvent<UID> implements SessionOutputEvent<UID>, TunnelContent<UID> {

    private CompletableFuture<Tunnel<UID>> sendFuture;

    private Message<UID> message;

    public SessionSendEvent(Tunnel<UID> tunnel, Message<UID> message, boolean sent, CompletableFuture<Tunnel<UID>> sendFuture) {
        super(tunnel);
        this.message = message;
        this.sendFuture = sendFuture;
        if (sent && sendFuture == null) {
            this.sendFuture = new CompletableFuture<>();
        }
    }

    @Override
    public Message<UID> getMessage() {
        return message;
    }

    @Override
    public boolean hasSendFuture() {
        return sendFuture != null;
    }

    @Override
    public SessionEventType getEventType() {
        return SessionEventType.MESSAGE;
    }

    @Override
    public void cancelSendWait(boolean mayInterruptIfRunning) {
        CompletableFuture<Tunnel<UID>> sendFuture = this.sendFuture;
        if (sendFuture != null && !sendFuture.isDone()) {
            sendFuture.cancel(mayInterruptIfRunning);
            this.sendFuture = null;
        }
    }

    @Override
    public void sendSuccess(Tunnel<UID> tunnel) {
        CompletableFuture<Tunnel<UID>> sendFuture = this.sendFuture;
        if (sendFuture != null && !sendFuture.isDone()) {
            sendFuture.complete(tunnel);
            this.sendFuture = null;
        }
    }

    @Override
    public void sendFailed(Throwable cause) {
        CompletableFuture<Tunnel<UID>> sendFuture = this.sendFuture;
        if (sendFuture != null && !sendFuture.isDone()) {
            sendFuture.completeExceptionally(cause);
            this.sendFuture = null;
        }
    }

    public void waitForSend(long timeout) throws InterruptedException, TimeoutException, ExecutionException {
        if (sendFuture != null)
            sendFuture.get(timeout, TimeUnit.MILLISECONDS);
    }
}
