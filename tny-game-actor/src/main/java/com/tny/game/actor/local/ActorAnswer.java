package com.tny.game.actor.local;

import com.tny.game.actor.Answer;
import com.tny.game.actor.AnswerListener;
import com.tny.game.common.concurrent.AbstractFuture;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ActorAnswer<T> extends AbstractFuture<T> implements Answer<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ActorAnswer.class);

    private volatile List<AnswerListener<T>> listeners;

    @Override
    public Done<T> getDone() {
        if (!this.isDone())
            return DoneUtils.fail();
        return DoneUtils.succNullable(this.getRawValue());
    }

    protected boolean success(T value) {
        if (super.set(value)) {
            fire();
            return true;
        }
        return false;
    }

    protected boolean fail(Throwable cause) {
        if (super.setException(cause)) {
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
