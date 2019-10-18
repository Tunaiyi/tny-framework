package com.tny.game.net.netty4;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.transport.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 */
public class NettyWriteMessagePromise extends AbstractFuture<Void> implements WriteMessagePromise, ChannelFutureListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyWriteMessagePromise.class);

    private long timeout;

    private volatile ChannelPromise channelPromise;

    private volatile RespondFuture<?> respondFuture;

    private volatile List<WriteMessageListener> listeners;

    private List<WriteMessageListener> listeners() {
        if (listeners != null)
            return listeners;
        synchronized (this) {
            if (listeners != null)
                return listeners;
            this.listeners = new LinkedList<>();
        }
        return listeners;
    }

    public NettyWriteMessagePromise(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean isSuccess() {
        return this.isDone() && this.getRawCause() == null;
    }

    @Override
    public Throwable cause() {
        return this.getRawCause();
    }

    @Override
    public long getWriteTimeout() {
        return timeout;
    }

    @Override
    public void addWriteListener(WriteMessageListener listener) {
        synchronized (this) {
            this.listeners().add(listener);
            if (this.isDone())
                fireListeners();
        }
    }

    @Override
    public void setRespondFuture(RespondFuture<?> respondFuture) {
        this.respondFuture = respondFuture;
    }

    public boolean channelPromise(ChannelPromise promise) {
        Throws.checkNotNull(promise, "channelPromise is null");
        if (this.isDone() || this.channelPromise != null)
            return false;
        synchronized (this) {
            if (this.isDone() || this.channelPromise != null)
                return false;
            this.channelPromise = promise;
            this.channelPromise.addListener(this);
            return true;
        }
    }

    @Override
    public void success() {
        if (this.isDone())
            return;
        synchronized (this) {
            if (this.isDone())
                return;
            if (this.channelPromise != null) {
                if (!this.channelPromise.isDone())
                    this.channelPromise.setSuccess();
            } else {
                this.set(null);
                this.fireListeners();
            }
        }
    }

    @Override
    public void failed(Throwable cause) {
        if (this.isDone())
            return;
        synchronized (this) {
            if (this.isDone())
                return;
            if (this.channelPromise != null) {
                if (!this.channelPromise.isDone())
                    this.channelPromise.setFailure(cause);
            } else {
                this.setFailure(cause);
                this.fireListeners();
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.isDone())
            return false;
        synchronized (this) {
            if (this.isDone())
                return false;
            if (this.channelPromise != null) {
                return this.channelPromise.cancel(mayInterruptIfRunning);
            } else {
                if (super.cancel(mayInterruptIfRunning)) {
                    this.fireListeners();
                    return true;
                }
                return false;
            }
        }
    }

    private void fireListeners() {
        if (!this.isDone())
            return;
        if (!this.isSuccess() && this.respondFuture != null)
            this.respondFuture.completeExceptionally(this.getRawCause());
        List<WriteMessageListener> listeners = this.listeners;
        if (listeners != null) {
            for (WriteMessageListener listener : listeners) {
                fireListener(listener);
            }
            listeners.clear();
        }
    }

    private void fireListener(WriteMessageListener listener) {
        try {
            listener.onWrite(this);
        } catch (Throwable e) {
            LOGGER.error("fire {} exception", listener, e);
        }
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        synchronized (this) {
            if (future.isSuccess()) {
                super.set(null);
            } else if (future.isCancelled()) {
                super.cancel(true);
            } else if (future.cause() != null) {
                super.setFailure(future.cause());
            }
            this.fireListeners();
        }
    }

}
