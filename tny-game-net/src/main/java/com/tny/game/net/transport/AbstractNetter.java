package com.tny.game.net.transport;

import com.tny.game.common.utils.StringAide;
import com.tny.game.net.exception.TunnelException;
import com.tny.game.net.message.*;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 11:50
 */
public abstract class AbstractNetter<UID> implements Netter<UID>, WriteCallback<UID> {

    private MessageIdCreator idCreator;

    private volatile RespondFutureHolder respondFutureHolder;

    protected AbstractNetter(int mark) {
        this.idCreator = new MessageIdCreator(mark);
    }

    protected long createMessageID() {
        return this.idCreator.createId();
    }

    protected RespondFutureHolder getOrCreateFutureHolder() {
        if (respondFutureHolder != null)
            return respondFutureHolder;
        return this.respondFutureHolder = RespondFutureHolder.getHolder(this);
    }

    protected void registerFuture(long messageId, RespondFuture<UID> respondFuture) {
        if (respondFuture == null)
            return;
        if (idCreator.isCreate(messageId))
            getOrCreateFutureHolder().putFuture(messageId, respondFuture);
    }

    protected void callbackFuture(Message<UID> message) {
        if (message.getMode() != MessageMode.RESPONSE)
            return;
        RespondFutureHolder holder = this.respondFutureHolder;
        if (holder == null)
            return;
        long messageId = message.getHeader().getToMessage();

        if (!idCreator.isCreate(messageId))
            return;
        RespondFuture<UID> future = holder.pollFuture(messageId);
        if (future != null)
            future.complete(message);
    }

    @Override
    public void onWrite(boolean success, Throwable cause, Message<UID> message, MessageContext<UID> context) {
        if (success) {
            MessageMode mode = message.getMode();
            if (mode == MessageMode.REQUEST)
                this.registerFuture(message.getId(), context.getRespondFuture());
            this.completeFuture(context.getSendFuture(), message);
        } else {
            this.completeExceptionally(context, new TunnelException(StringAide.format("message {} write failed"), message, cause));
        }
    }

    protected void destroyFutureHolder() {
        RespondFutureHolder.removeHolder(this);
    }

    protected <T> void cancelFuture(CompletableFuture<T> future) {
        if (future != null)
            future.cancel(true);
    }

    protected <T> void completeFuture(CompletableFuture<T> future, T message) {
        if (future != null)
            future.complete(message);
    }

    protected <T> void completeExceptionally(CompletableFuture<T> future, Throwable e) {
        if (future != null)
            future.completeExceptionally(e);
    }

    protected <T> void completeCancel(MessageContext<UID> context) {
        if (context == null)
            return;
        cancelFuture(context.getSendFuture());
        cancelFuture(context.getRespondFuture());
    }

    protected void completeFuture(MessageContext<UID> context, Message<UID> message) {
        if (context == null)
            return;
        completeFuture(context.getSendFuture(), message);
        completeFuture(context.getRespondFuture(), message);
    }

    protected void completeExceptionally(MessageContext<UID> context, Throwable e) {
        if (context == null)
            return;
        completeExceptionally(context.getSendFuture(), e);
        completeExceptionally(context.getRespondFuture(), e);
    }

}
