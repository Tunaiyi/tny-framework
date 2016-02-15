package com.tny.game.actor.local.reason;

/**
 * 挂起原因类型
 *
 * @author KGTny
 */
public enum SuspendReasonType {

    /**
     * 重创失败
     */
    RECREATE(true),

    /**
     * 创建失败
     */
    CREATION(true),

    /**
     * Actor终止中
     */
    TERMINATION,

    /**
     * Actor终止
     */
    TERMINATED;

    private final boolean waitingForChildren;

    SuspendReasonType() {
        this(false);
    }

    SuspendReasonType(boolean waitingForChildren) {
        this.waitingForChildren = waitingForChildren;
    }

    public boolean isWaitingForChildren() {
        return waitingForChildren;
    }

}
