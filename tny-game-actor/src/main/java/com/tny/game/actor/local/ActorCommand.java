package com.tny.game.actor.local;

import com.tny.game.actor.Answer;
import com.tny.game.actor.exception.ActorCommandCancelledException;
import com.tny.game.actor.exception.ActorCommandExecuteException;
import com.tny.game.actor.stage.Flows;
import com.tny.game.actor.stage.Stage;
import com.tny.game.actor.stage.exception.TaskInterruptedException;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;
import com.tny.game.worker.command.Command;

/**
 * Actor命令
 * Created by Kun Yang on 16/4/26.
 */
@SuppressWarnings("unchecked")
public abstract class ActorCommand<T, TS extends Stage, A extends Answer<T>> implements Command {

    protected ActorCell actorCell;

    protected TS stage;

    private Done<Object> result = DoneUtils.fail();

    private Done<T> handleResult = DoneUtils.fail();

    private volatile boolean done = false;

    private volatile Throwable cause;

    private boolean cancelled;

    protected ActorCommand(ActorCell actorCell) {
        this.actorCell = actorCell;
    }

    protected ActorCommand(ActorCell actorCell, TS stage) {
        this.actorCell = actorCell;
        if (stage != null)
            this.setStage(stage);
    }

    protected void setStage(TS stage) {
        this.stage = stage;
    }

    @Override
    public void execute() {
        if (done)
            return;
        actorCell.execute(this);
    }


    @SuppressWarnings("unchecked")
    void doExecute() throws Throwable {
        if (done)
            return;
        synchronized (this) {
            if (done)
                return;
            if (cancelled) {
                if (this.stage != null) {
                    Flows.cancel(stage);
                    this.checkStageResult();
                } else {
                    this.fail(new ActorCommandCancelledException(this), true);
                }
            } else if (!this.handleResult.isSuccess()) {
                Done<Object> result = null;
                try {
                    result = this.doHandle();
                } catch (Throwable e) {
                    this.fail(e, false);
                }
                if (result != null && result.isSuccess()) {
                    this.setHandleResult((Done<T>) result);
                    if (this.stage == null)
                        this.success((T) result.get());
                }
            }
            if (this.handleResult.isSuccess() && stage != null) {
                if (!this.stage.isDone()) {
                    Flows.process(this.stage);
                    boolean stageDone = this.stage.isDone();
                    if (!stageDone)
                        return;
                    this.checkStageResult();
                }
            }
        }
    }

    private void checkStageResult() throws Throwable {
        if (this.stage.isFailed()) {
            Throwable cause = stage.getCause();
            this.fail(cause, cause instanceof TaskInterruptedException);
        } else {
//            T result = null;
//            if (this.stage instanceof TypeTaskStage)
//                result = ((TypeTaskStage<T>) this.stage).getResult();
            this.success(handleResult.get());
        }
    }

    /**
     * 设置成功结果值
     *
     * @param result 结果值
     * @return 返回是否成功
     */
    protected void success(T result) {
        if (!done) {
            this.done = true;
            this.result = DoneUtils.succNullable(result);
            this.cause = null;
            this.postSuccess(result);
        }
    }

    /**
     * 设置成功结果值
     *
     * @param result 结果值
     * @return 返回是否成功
     */
    protected void setHandleResult(Done<T> result) {
        if (!done)
            this.handleResult = result;
    }

    public Done<T> getHandleResult() {
        return this.handleResult;
    }

    public boolean isHandled() {
        return this.handleResult.isSuccess();
    }

    /**
     * 设置失败原因
     *
     * @param cause 失败原因
     * @return 设置成功
     */
    protected void fail(Throwable cause, boolean cancelled) throws Throwable {
        if (!done) {
            this.done = true;
            this.result = DoneUtils.succNullable(null);
            this.cause = new ActorCommandExecuteException(this, cause);
            if (cancelled)
                this.postCancel();
            this.postFail(this.cause);
            throw this.cause;
        }
    }

    protected void postSuccess(T result) {
    }

    protected void postFail(Throwable cause) {
    }

    protected void postCancel() {
    }

    public boolean cancel() {
        if (done)
            return false;
        synchronized (this) {
            if (done)
                return false;
            this.cancelled = true;
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public Object getResult() {
        if (isDone()) {
            if (this.stage != null) {
                if (this.stage instanceof Stage)
                    return ((Stage<T>) this.stage).getResult();
                return null;
            } else {
                return this.result.get();
            }
        }
        return null;
    }

    public A getAnswer() {
        return null;
    }

    protected abstract Done<Object> doHandle() throws Exception;

    @Override
    public boolean isDone() {
        return done;
    }


}
