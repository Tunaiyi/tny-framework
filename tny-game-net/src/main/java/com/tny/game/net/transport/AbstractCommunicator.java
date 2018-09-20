package com.tny.game.net.transport;

import com.tny.game.net.transport.message.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 11:50
 */
public abstract class AbstractCommunicator<UID> implements Communicator<UID> {

    private MessageIdCreator idCreator;

    private volatile RespondFutureHolder respondFutureHolder;

    protected AbstractCommunicator(int mark) {
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
        getOrCreateFutureHolder().putFuture(messageId, respondFuture);
    }

    protected RespondFuture<UID> removeFuture(long messageId) {
        RespondFutureHolder holder = this.respondFutureHolder;
        if (holder == null)
            return null;
        return holder.pollFuture(messageId);
    }

    protected void destroyFutureHolder() {
        RespondFutureHolder.removeHolder(this);
    }

}
