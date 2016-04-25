package com.tny.game.actor.task;

/**
 * Created by Kun Yang on 16/1/22.
 */
public abstract class BaseTaskStage<R> implements InnerTaskStage<R> {

    protected CommonTaskStage head;

    protected CommonTaskStage next;

    public BaseTaskStage(CommonTaskStage head) {
        this.head = head == null ? this : head;
    }

    @Override
    public CommonTaskStage getHead() {
        return head;
    }

    @Override
    public void start() {
        this.head.run(null, null, null, null, Stages.KEY);
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
    public <T extends TaskStage> T setNext(T next, TaskStageKey key) {
        Stages.checkKey(key);
        this.next = StageUtils.as(next);
        return next;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void run(TaskFragment<?, ?> prev, Object returnVal, Throwable e, TaskContext context, TaskStageKey key) {
        TaskFragment<Object, ?> fragment = (TaskFragment<Object, ?>) getTaskFragment();
        while (true) {
            if (fragment.isDone()) {
                if (next != null && next.isCanRun(fragment)) {
                    if (next.isNoneParam())
                        next.run(fragment, null, fragment.getCause(), context, key);
                    else
                        next.run(fragment, fragment.getResult(), fragment.getCause(), context, key);
                }
                return;
            } else {
                fragment.execute(returnVal, e, context);
                if (!fragment.isDone())
                    return;
            }
        }
    }

}
