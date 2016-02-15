package com.tny.game.actor.local;

import com.tny.game.actor.local.reason.CreationReason;
import com.tny.game.actor.local.reason.RecreateReason;
import com.tny.game.actor.local.reason.SuspendReason;
import com.tny.game.actor.local.reason.TerminatedReason;

/**
 * Actor子接点容器状态
 * Created by Kun Yang on 16/1/15.
 */


abstract class ReasonState implements ChildrenContainerState {

    protected SuspendReason reason;

    protected ReasonState(SuspendReason reason) {
        this.reason = reason;
    }

    @Override
    public boolean isHasReason() {
        return true;
    }

    @Override
    public SuspendReason getReason() {
        return reason;
    }
}

class NormalState implements ChildrenContainerState {

    static final NormalState NORMAL = new NormalState();

    private NormalState() {
    }

}

class SuspendState extends ReasonState {

    protected SuspendState(CreationReason reason) {
        super(reason);
    }

    protected SuspendState(RecreateReason reason) {
        super(reason);
    }

    protected SuspendState(SuspendReason reason) {
        super(reason);
    }

}

class TerminatingState extends ReasonState {

    static final TerminatingState TERMINATING = new TerminatingState();

    private TerminatingState() {
        super(TerminatedReason.instance());
    }

    @Override
    public boolean isTerminating() {
        return true;
    }

    @Override
    public boolean isNormal() {
        return false;
    }
}

class TerminatedState extends ReasonState {

    static final TerminatedState TERMINATED = new TerminatedState();

    private TerminatedState() {
        super(TerminatedReason.instance());
    }

    @Override
    public boolean isTerminating() {
        return true;
    }

    @Override
    public boolean isNormal() {
        return false;
    }

}

public interface ChildrenContainerState {

    static ChildrenContainerState state(SuspendReason reason) {
        switch (reason.getReasonType()) {
            case CREATION:
                return new SuspendState(reason);
            case RECREATE:
                return new SuspendState(reason);
            case TERMINATION:
                return terminating();
            case TERMINATED:
                return terminated();
            default:
                return normal();
        }
    }

    static ChildrenContainerState suspend(RecreateReason reason) {
        return new SuspendState(reason);
    }

    static ChildrenContainerState suspend(CreationReason reason) {
        return new SuspendState(reason);
    }

    static ChildrenContainerState terminating() {
        return TerminatingState.TERMINATING;
    }

    static ChildrenContainerState terminated() {
        return TerminatedState.TERMINATED;
    }

    static ChildrenContainerState normal() {
        return NormalState.NORMAL;
    }

    default boolean isNormal() {
        return true;
    }

    default boolean isTerminating() {
        return false;
    }

    default boolean isHasReason() {
        return false;
    }

    default SuspendReason getReason() {
        return null;
    }


}
