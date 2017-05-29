package com.tny.game.actor.stage;


import com.tny.game.actor.stage.exception.TaskInterruptedException;
import com.tny.game.common.reflect.ObjectUtils;

/**
 * 基础阶段抽象类
 * Created by Kun Yang on 16/1/22.
 */
@SuppressWarnings("unchecked")
public abstract class BaseStage<R> implements InnerStage<R> {

    protected CommonStage head;

    protected CommonStage next;

    protected BaseStage(CommonStage head) {
        this.head = head == null ? this : head;
    }

    @Override
    public CommonStage getHead() {
        return head;
    }

    @Override
    public void start() {
        this.head.run(null, null, null, Stages.KEY);
    }

    @Override
    public void cancel() {
        this.head.interrupt();
    }

    @Override
    public CommonStage getNext() {
        return next;
    }

    @Override
    public void setHead(CommonStage head, TaskStageKey key) {
        Stages.checkKey(key);
        this.head = head;
    }

    @Override
    public <T extends Stage, OT extends T> T setNext(OT next, TaskStageKey key) {
        Stages.checkKey(key);
        this.next = ObjectUtils.as(next);
        return next;
    }

    @Override
    public void interrupt() {
        TaskFragment<Object, ?> fragment = (TaskFragment<Object, ?>) getTaskFragment();
        if (!fragment.isDone()) {
            fragment.fail(new TaskInterruptedException("stage was interrupted"));
        } else {
            if (next != null)
                this.next.interrupt();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(TaskFragment<?, ?> prev, Object returnVal, Throwable e, TaskStageKey key) {
        TaskFragment<Object, ?> fragment = (TaskFragment<Object, ?>) getTaskFragment();
        while (true) {
            if (fragment.isDone()) {
                if (next != null && next.isCanRun(fragment)) {
                    if (next.isNoneParam())
                        next.run(fragment, null, fragment.getCause(), key);
                    else
                        next.run(fragment, fragment.getResult(), fragment.getCause(), key);
                }
                return;
            } else {
                fragment.execute(returnVal, e);
                if (!fragment.isDone())
                    return;
            }
        }
    }

}
