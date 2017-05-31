package com.tny.game.actor.stage;

import com.tny.game.common.reflect.ObjectUtils;

/**
 * Created by Kun Yang on 2017/5/31.
 */
public class LinkedFlow implements Flow {

    private static final byte IDLE = 0;
    private static final byte EXECUTE = 1;
    private static final byte DONE = 1 << 2;
    private static final byte SUCCESS = 1 << 3 | DONE;
    private static final byte FAILED = 1 << 3 | DONE;

    private InnerStage head;

    private InnerStage tail;

    private Fragment<?, ?> previous;

    private InnerStage current;

    private byte state;

    private boolean cancel;

    @Override
    public boolean isDone() {
        return (state & DONE) == DONE;
    }

    @Override
    public boolean isFailed() {
        return (state & FAILED) == FAILED;
    }

    @Override
    public boolean isSuccess() {
        return (state & SUCCESS) == SUCCESS;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public void cancel() {
        if (!this.isDone()) {
            this.cancel = true;
        }
    }

    @Override
    public <T> Stage<T> first(Object name) {
        InnerStage<?> stage = head;
        while (stage != null && (stage.getName() == null || !stage.getName().equals(name))) {
            stage = stage.getNext();
        }
        return ObjectUtils.as(stage);
    }

    @Override
    public void run() {
        while (!this.isDone()) {
            if (current.isDone()) {
                this.previous = current.getFragment();
                this.current = current.getNext();
            } else {
                if (!this.current.isCanRun(this.previous)) {

                } else {
                    Fragment<?, ?> fragment = null;
                    Throwable cause = null;
                    if (this.previous != null) {
                        fragment = this.previous.getFragment();
                        returnValue = this.previous.getResult();
                        cause = this.previous.getCause();
                    }
                    this.current.run(fragment, returnValue, cause);
                }
            }
        }
    }
}
