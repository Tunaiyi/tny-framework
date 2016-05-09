package com.tny.game.actor.stage;


import com.tny.game.actor.stage.exception.TaskInterruptedException;

/**
 * 基础阶段抽象类
 * Created by Kun Yang on 16/1/22.
 */
@SuppressWarnings("unchecked")
public abstract class BaseTaskStage<R> implements InnerTaskStage<R> {

    protected CommonTaskStage head;

    protected CommonTaskStage next;

    protected BaseTaskStage(CommonTaskStage head) {
        this.head = head == null ? this : head;
    }

    @Override
    public CommonTaskStage getHead() {
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
    public CommonTaskStage getNext() {
        return next;
    }

    @Override
    public void setHead(CommonTaskStage head, TaskStageKey key) {
        Stages.checkKey(key);
        this.head = head;
    }

    @Override
    public <T extends TaskStage, OT extends T> T setNext(OT next, TaskStageKey key) {
        Stages.checkKey(key);
        this.next = StageUtils.as(next);
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
