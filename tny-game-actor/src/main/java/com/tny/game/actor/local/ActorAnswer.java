package com.tny.game.actor.local;

import com.tny.game.actor.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ActorAnswer<T> extends AbstractFuture<T> implements Answer<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ActorAnswer.class);

    private volatile List<AnswerListener<T>> listeners;

    @Override
    public Done<T> getDone() {
        if (!this.isDone())
            return DoneResults.failure();
        return DoneResults.successNullable(this.getRawValue());
    }

    protected boolean success(T value) {
        if (super.set(value)) {
            fire();
            return true;
        }
        return false;
    }

    protected boolean fail(Throwable cause) {
        if (super.setFailure(cause)) {
            fire();
            return true;
        }
        return false;
    }

    @Override
    public Throwable getCause() {
        return this.getRawCause();
    }

    @Override
    public boolean isFail() {
        return isDone() && this.getRawCause() != null;
    }

    @Override
    public void addListener(AnswerListener<T> listener) {
        if (this.listeners == null)
            this.listeners = new CopyOnWriteArrayList<>();
        this.listeners.add(listener);
    }

    private void fire() {
        if (this.listeners == null)
            return;
        this.listeners.forEach(l -> {
            try {
                l.onDone(this, getRawValue(), getRawCause(), isCancelled());
            } catch (Throwable e) {
                LOGGER.error("{}.done 异常", l.getClass(), e);
            }
        });
    }
}
