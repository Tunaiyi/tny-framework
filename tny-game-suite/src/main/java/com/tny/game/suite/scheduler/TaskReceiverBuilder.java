package com.tny.game.suite.scheduler;

import com.tny.game.common.scheduler.*;

public class TaskReceiverBuilder {

    /**
     * 接收者ID
     *
     * @uml.property name="receicerId"
     */
    protected long playerId;

    /**
     * 任务组
     *
     * @uml.property name="group"
     */
    protected ReceiverType group;

    /**
     * 最后处理任务的时间
     *
     * @uml.property name="lastHandlerTime"
     */
    protected long lastHandlerTime = -1L;

    /**
     * 最后一次实际处理时间
     */
    protected long actualLastHandlerTime = -1L;

    private TaskReceiverBuilder() {
        super();
    }

    public static TaskReceiverBuilder create() {
        return new TaskReceiverBuilder();
    }

    public TaskReceiverBuilder setPlayerId(long playerId) {
        this.playerId = playerId;
        return this;
    }

    public TaskReceiverBuilder setGroup(ReceiverType group) {
        this.group = group;
        return this;
    }

    public TaskReceiverBuilder setLastHandlerTime(long lastHandlerTime) {
        this.lastHandlerTime = lastHandlerTime;
        return this;
    }

    public TaskReceiverBuilder setActualLastHandlerTime(long actualLastHandlerTime) {
        this.actualLastHandlerTime = actualLastHandlerTime;
        return this;
    }

    public TaskReceiver build() {
        GameTaskReceiver receiver = new GameTaskReceiver();
        receiver.setPlayerId(this.playerId);
        receiver.setActualLastHandlerTime(this.actualLastHandlerTime == -1 ? System.currentTimeMillis() : this.actualLastHandlerTime);
        receiver.setLastHandlerTime(this.lastHandlerTime == -1 ? System.currentTimeMillis() : this.lastHandlerTime);
        if (this.group == null)
            throw new NullPointerException("group is null");
        receiver.setGroup(this.group);
        return receiver;
    }
}
