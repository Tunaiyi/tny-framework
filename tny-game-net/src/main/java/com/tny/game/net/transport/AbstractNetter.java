package com.tny.game.net.transport;

import com.tny.game.common.context.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 11:50
 */
public abstract class AbstractNetter<UID> extends AttributesHolder implements Netter<UID> {

    protected AbstractNetter() {
    }

    protected void destroyFutureHolder() {
        RespondFutureHolder.removeHolder(this);
    }

    @Override
    public UID getUserId() {
        return getCertificate().getUserId();
    }

    @Override
    public String getUserType() {
        return getCertificate().getUserType();
    }

    @Override
    public boolean isLogin() {
        return this.getCertificate().isAuthenticated();
    }

    // protected <T> void cancelFuture(CompletableFuture<T> future) {
    //     if (future != null)
    //         future.cancel(true);
    // }
    //
    // protected <T> void completeFuture(CompletableFuture<T> future, T message) {
    //     if (future != null)
    //         future.complete(message);
    // }
    //
    // protected <T> void completeExceptionally(CompletableFuture<T> future, Throwable e) {
    //     if (future != null)
    //         future.completeExceptionally(e);
    // }
    //
    // protected <T> void completeCancel(MessageContext<UID> context) {
    //     if (context == null)
    //         return;
    //     cancelFuture(context.getSendFuture());
    //     cancelFuture(context.getRespondFuture());
    // }
    //
    // protected void completeFuture(MessageContext<UID> context, Message<UID> message) {
    //     if (context == null)
    //         return;
    //     completeFuture(context.getSendFuture(), message);
    //     completeFuture(context.getRespondFuture(), message);
    // }
    //
    // protected void completeExceptionally(MessageContext<UID> context, Throwable e) {
    //     if (context == null)
    //         return;
    //     completeExceptionally(context.getSendFuture(), e);
    //     completeExceptionally(context.getRespondFuture(), e);
    // }

}
