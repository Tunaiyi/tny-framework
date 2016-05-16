package com.tny.game.actor.local;

import com.tny.game.actor.Answer;
import com.tny.game.actor.stage.TaskStage;
import com.tny.game.common.concurrent.AbstractFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BaseAnswer<T, TS extends TaskStage> extends AbstractFuture<T> implements Answer<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseAnswer.class);

    protected volatile TS stage;

    protected void setStage(TS stage) {
        this.stage = stage;
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

    protected abstract void fire();

    @Override
    public Throwable getCause() {
        return this.getRawCause();
    }

    @Override
    public boolean isFail() {
        return isDone() && this.getRawCause() != null;
    }
}
