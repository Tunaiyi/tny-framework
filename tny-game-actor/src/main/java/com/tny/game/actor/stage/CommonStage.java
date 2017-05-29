package com.tny.game.actor.stage;

/**
 * Created by Kun Yang on 16/1/22.
 */
interface CommonStage extends Stage {

    CommonStage getHead();

    CommonStage getNext();

    default Object getTailResult() {
        CommonStage next = getNext();
        TaskFragment<?, ?> fragment = getTaskFragment();
        if (!fragment.isDone() || fragment.isFailed()) {
            return null;
        } else if (next != null) {
            return next.getTailResult();
        } else {
            return fragment.getResult();
        }
    }

    void setHead(CommonStage head, TaskStageKey key);

    <T extends Stage, OT extends T> T setNext(OT next, TaskStageKey key);

    boolean isNoneParam();

    default boolean isCanRun(TaskFragment<?, ?> prev) {
        return prev == null || checkCanRun(prev);
    }

    default boolean checkCanRun(TaskFragment<?, ?> prev) {
        return prev.isSuccess();
    }

    void run(TaskFragment<?, ?> prev, Object returnVal, Throwable e, TaskStageKey key);

    void interrupt();

    TaskFragment<?, ?> getTaskFragment();

    default boolean isAllDone() {
        CommonStage next = getNext();
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
        CommonStage next = getNext();
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
        CommonStage next = getNext();
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

    void cancel();

    default boolean isCurrnetDone() {
        return getTaskFragment().isDone();
    }

    @Override
    default boolean isDone() {
        return this.getHead().isAllDone();
    }

    @Override
    default boolean isFailed() {
        return this.getHead().isAllFailed();
    }

    @Override
    default boolean isSuccess() {
        return this.getHead().isAllSuccess();
    }

    @Override
    default Throwable getCause() {
        return this.getHead().getFirstCause();
    }

    default Object getResult() {
        return this.getHead().getTailResult();
    }

}
