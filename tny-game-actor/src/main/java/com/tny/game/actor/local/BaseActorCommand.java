package com.tny.game.actor.local;

import com.tny.game.actor.*;
import com.tny.game.actor.exception.*;
import com.tny.game.common.result.*;

/**
 * Actor命令
 * Created by Kun Yang on 16/4/26.
 */
@SuppressWarnings("unchecked")
public abstract class BaseActorCommand<T> extends ActorCommand<T> {

    private ActorAnswer<T> answer;

    private T result;

    protected volatile boolean done = false;

    private volatile Throwable cause;

    private boolean cancelled;

    protected BaseActorCommand(ActorCell actorCell) {
        super(actorCell);
    }

    protected BaseActorCommand(ActorCell actorCell, ActorAnswer<T> answer) {
        super(actorCell);
        this.answer = answer;
    }

    @Override
    public T getResult() {
        return this.result;
    }

    @Override
    public Answer<T> getAnswer() {
        return this.answer;
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

    @Override
    protected void setAnswer(ActorAnswer<T> answer) {
        if (this.answer == null) {
            this.answer = answer;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handle() throws Throwable {
        if (isDone()) {
            return;
        }
        synchronized (this) {
            if (isDone()) {
                return;
            }
            if (this.cancelled) {
                this.fail(new ActorCommandCancelledException(this), true);
            } else {
                Done<T> result = null;
                try {
                    result = this.doHandle();
                } catch (Throwable e) {
                    this.fail(e, false);
                }
                if (result != null && result.isSuccess()) {
                    this.success(result.get());
                }
            }
        }
    }

    protected abstract Done<T> doHandle() throws Exception;

    /**
     * 设置成功结果值
     *
     * @param result 结果值
     * @return 返回是否成功
     */
    protected void success(T result) {
        if (!this.done) {
            this.done = true;
            this.result = result;
            this.cause = null;
            this.postSuccess(result);
            if (this.answer != null) {
                this.answer.success(result);
            }
        }
    }

    /**
     * 设置失败原因
     *
     * @param cause 失败原因
     * @return 设置成功
     */
    protected void fail(Throwable cause, boolean cancelled) throws Throwable {
        if (!this.done) {
            this.done = true;
            this.result = null;
            this.cause = new ActorCommandExecuteException(this, cause);
            if (cancelled) {
                this.postCancel();
            }
            this.postFail(this.cause);
            if (this.answer != null) {
                this.answer.fail(cause);
            }
            throw this.cause;
        }
    }

    @Override
    protected boolean cancel() {
        if (this.done) {
            return false;
        }
        synchronized (this) {
            if (this.done) {
                return false;
            }
            this.cancelled = true;
            return true;
        }
    }

    protected void postSuccess(T result) {
    }

    protected void postFail(Throwable cause) {
    }

    protected void postCancel() {
    }

}
