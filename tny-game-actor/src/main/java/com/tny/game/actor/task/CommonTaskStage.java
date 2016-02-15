package com.tny.game.actor.task;

/**
 * Created by Kun Yang on 16/1/22.
 */
public interface CommonTaskStage {

    CommonTaskStage getHead();

    CommonTaskStage getNext();

    default Object getFinalResult() {
        return this.getHead().getTailResult();
    }

    default Object getTailResult() {
        CommonTaskStage next = getNext();
        TaskFragment<?, ?> fragment = getTaskFragment();
        if (!fragment.isDone() || fragment.isFailed()) {
            return null;
        } else if (next != null) {
            return next.getTailResult();
        } else {
            return fragment.getResult();
        }
    }

    void setHead(CommonTaskStage head, TaskStageKey key);

    <T extends CommonTaskStage> T setNext(T next, TaskStageKey key);

    boolean isNoneParam();

    default boolean isCanRun(TaskFragment<?, ?> prev) {
        return prev == null || checkCanRun(prev);
    }

    default boolean checkCanRun(TaskFragment<?, ?> prev) {
        return prev.isSuccess();
    }

    void run(TaskFragment<?, ?> prev, Object returnVal, Throwable e, TaskContext context, TaskStageKey key);

    TaskFragment<?, ?> getTaskFragment();

    default boolean isAllDone() {
        CommonTaskStage next = getNext();
        TaskFragment<?, ?> fragment = getTaskFragment();
        if (!fragment.isDone())
            return false;
        else if (fragment.isFailed()) {
            return true;
        }
        if (next != null) {
            return next.isAllDone();
        } else {
            return true;
        }
    }

    default boolean isAllFailed() {
        return !isAllSuccess();
    }

    default boolean isAllSuccess() {
        CommonTaskStage next = getNext();
        TaskFragment<?, ?> fragment = getTaskFragment();
        if (!fragment.isSuccess())
            return false;
        else if (next != null) {
            return next.isAllSuccess();
        } else {
            return true;
        }
    }

    default Throwable getFirstCause() {
        CommonTaskStage next = getNext();
        TaskFragment<?, ?> fragment = getTaskFragment();
        if (fragment.isDone() && fragment.isFailed()) {
            return fragment.getCause();
        } else if (next != null) {
            return next.getFirstCause();
        } else {
            return null;
        }
    }

    void start();

    default boolean isDone() {
        return getTaskFragment().isDone();
    }

    default boolean isFinalDone() {
        return this.getHead().isAllDone();
    }

    default boolean isFinalFailed() {
        return this.getHead().isAllFailed();
    }

    default boolean isFinalSuccess() {
        return this.getHead().isAllSuccess();
    }


    default Throwable getFinalCause() {
        return this.getHead().getFirstCause();
    }


}
