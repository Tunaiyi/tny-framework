package com.tny.game.actor.local;

import com.tny.game.actor.*;
import com.tny.game.actor.local.queue.DefaultSystemMessageQueue;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认的信箱实现
 * Created by Kun Yang on 16/1/19.
 */
public class DefaultPostbox extends Postbox {

    protected AtomicInteger status = new AtomicInteger(Postbox.OPEN);

    private MessageQueue messageQueue;

    private SystemMessageQueue systemMessageQueue;

    private ActorCell actor;

    public DefaultPostbox(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void bind(ActorCell actor) {
        this.actor = actor;
        this.systemMessageQueue = new DefaultSystemMessageQueue(actor);
    }

    @Override
    public MessagePostman getPostman() {
        return actor.getPostman();
    }

    @Override
    protected ActorCell getActorCell() {
        return actor;
    }

    @Override
    public void enqueue(ActorRef receiver, Envelope envelope) {
        messageQueue.enqueue(receiver, envelope);
    }


    @Override
    public boolean hasMessages() {
        return messageQueue.hasMessages();
    }

    @Override
    public int messageSize() {
        return messageQueue.size();
    }

    public int getStatus() {
        return status.get();
    }

    public boolean shouldProcessMessage() {
        return (status.get() & SHOULD_NOT_PROCESS_MASK) == 0;
    }

    @Override
    public int suspendCount() {
        return status.get() / SUSPEND_UNIT;
    }

    @Override
    public boolean isSuspended() {
        return (status.get() & SUSPEND_MASK) != 0;
    }

    @Override
    public boolean isClosed() {
        return status.get() == CLOSED;
    }

    @Override
    public boolean isScheduled() {
        return (status.get() & SCHEDULED) != 0;
    }

    @Override
    public boolean resume() {
        while (true) {
            int nowStatus = this.status.get();
            switch (nowStatus) {
                case CLOSED:
                    this.status.set(CLOSED);
                    return false;
                default:
                    int next = nowStatus < SUSPEND_UNIT ? nowStatus : nowStatus - SUSPEND_UNIT;
                    if (status.compareAndSet(nowStatus, next))
                        return next < SUSPEND_UNIT;
            }
        }
    }

    @Override
    public boolean suspend() {
        while (true) {
            int nowStatus = this.status.get();
            switch (nowStatus) {
                case CLOSED:
                    this.status.set(CLOSED);
                    return false;
                default:
                    if (status.compareAndSet(nowStatus, nowStatus + SUSPEND_UNIT))
                        return nowStatus < SUSPEND_UNIT;
            }
        }
    }


    @Override
    public boolean close() {
        while (true) {
            int nowStatus = this.status.get();
            switch (nowStatus) {
                case CLOSED:
                    this.status.set(CLOSED);
                    return false;
                default:
                    if (status.compareAndSet(nowStatus, CLOSED))
                        return true;
            }
        }
    }

    @Override
    protected boolean setAsScheduled() {
        while (true) {
            int nowStatus = status.get();
            if ((nowStatus & SHOULD_SCHEDULE_MASK) != OPEN)
                return false;
            else if (status.compareAndSet(nowStatus, nowStatus | SCHEDULED))
                return true;
        }
    }

    @Override
    protected boolean setAsIdle() {
        while (true) {
            int nowStatus = status.get();
            if (status.compareAndSet(nowStatus, nowStatus & ~SCHEDULED))
                return true;
        }
    }

    @Override
    protected final boolean canBeScheduledForExecution(boolean hasMessageHint, boolean hasSystemMessageHint) {
        int nowStatus = status.get();
        switch (nowStatus) {
            case OPEN:
            case SCHEDULED:
                return hasMessageHint || hasSystemMessageHint || hasSystemMessages() || hasMessages();
            case CLOSED:
                return false;
            default:
                return hasSystemMessages() || hasMessages();
        }
    }

    @Override
    public void run() {
        try {
            if (!isClosed()) {
                processAllSystemMessages();
                MessagePostman postman = getPostman();
                processMailbox(postman.getThroughput(), postman.getThroughputDeadlineTime().toMillis());
            }
        } finally {
            setAsIdle();
            getPostman().registerForExecution(this, false, false);
        }
    }

    private void processMailbox(int throughput, long throughputDeadline) {

    }

    private void processAllSystemMessages() {
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public void systemEnqueue(ActorRef receiver, SystemMessage message) {

    }


    @Override
    public boolean hasSystemMessages() {
        return false;
    }


}
