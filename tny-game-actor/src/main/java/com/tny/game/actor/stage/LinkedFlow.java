package com.tny.game.actor.stage;

import com.tny.game.actor.stage.exception.FlowBreakOffException;
import com.tny.game.actor.stage.exception.FlowCancelException;
import com.tny.game.common.reflect.ObjectUtils;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;

/**
 * Created by Kun Yang on 2017/5/31.
 */
public class LinkedFlow<V> implements InnerFlow<V> {

    private static final byte IDLE = 0;
    private static final byte EXECUTE = 1;
    private static final byte DONE = 1 << 2;
    private static final byte SUCCESS = 1 << 3 | DONE;
    private static final byte FAILED = DONE;

    private InnerStage head;

    private InnerStage tail;

    private InnerStage current;

    private Fragment<?, ?> previous;

    private byte state = IDLE;

    private Throwable cause;

    private Object result;

    private boolean cancel;

    @Override
    public boolean isDone() {
        return (state & DONE) == DONE;
    }

    @Override
    public boolean isFailed() {
        return state == FAILED;
    }

    @Override
    public boolean isSuccess() {
        return state == SUCCESS;
    }

    @Override
    public Throwable getCause() {
        if (this.isFailed())
            return cause;
        return null;
    }

    @Override
    public void cancel() {
        if (this.state == EXECUTE) {
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
            if (state == IDLE)
                state = EXECUTE;
            if (current.isDone()) {
                this.previous = current.getFragment();
                this.current = current.getNext();
                if (this.current == null) { // 最后的stage Flow结束
                    if (this.previous.isFailed()) { // 失败
                        this.state = FAILED;
                        this.cause = this.previous.getCause();
                    } else if (this.previous.isSuccess()) { // 成功
                        this.state = SUCCESS;
                        this.result = this.previous.getResult();
                    }
                    return;
                }
            } else {
                if (this.cancel) { // Flow取消
                    this.current.interrupt();
                    this.cause = new FlowCancelException();
                    this.state = FAILED;
                    return;
                }
                if (!this.current.isCanRun(this.previous)) { // 无法继续运行
                    this.state = FAILED;
                    if (this.previous != null)
                        this.cause = this.previous.getCause();
                    if (this.cause == null) {
                        this.cause = new FlowBreakOffException();
                    }
                } else { // 运行
                    Throwable cause = null;
                    Object returnValue = null;
                    if (this.previous != null) {
                        returnValue = this.previous.getResult();
                        cause = this.previous.getCause();
                    }
                    this.current.run(returnValue, cause);
                    if (!this.current.isDone())
                        break;
                }
            }
        }
    }

    @Override
    public Done<V> getResult() {
        if (this.isFailed())
            return DoneUtils.fail();
        else if (this.isSuccess())
            return DoneUtils.succNullable(ObjectUtils.as(this.previous.getResult()));
        else
            return DoneUtils.succNullable(null);
    }

    @Override
    public <F extends Flow> F add(InnerStage<?> stage) {
        if (this.head == null) {
            this.head = stage;
            this.tail = stage;
            this.current = this.head;
        } else {
            this.tail.setNext(stage);
            this.tail = stage;
        }
        while (this.tail.hasNext())
            this.tail = this.tail.getNext();
        return ObjectUtils.as(this);
    }

    @Override
    public Fragment<?, ?> getTaskFragment() {
        return null;
    }
}
