package com.tny.game.actor.local;

import com.tny.game.actor.Answer;
import com.tny.game.actor.stage.Stage;
import com.tny.game.common.concurrent.AbstractFuture;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BaseAnswer<T, TS extends Stage> extends AbstractFuture<T> implements Answer<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseAnswer.class);

    protected volatile TS stage;

    protected void setStage(TS stage) {
        this.stage = stage;
    }

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
