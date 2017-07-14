package com.tny.game.suite.scheduler;

import com.tny.game.common.scheduler.TaskReceiver;

public class TaskReceiverBuilder {

    /**
     * 接收者ID
     *
     * @uml.property name="receicerId"
     */
    protected long playerID;

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

    public TaskReceiverBuilder setPlayerID(long playerID) {
        this.playerID = playerID;
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
        receiver.setPlayerID(playerID);
        receiver.setActualLastHandlerTime(actualLastHandlerTime == -1 ? System.currentTimeMillis() : actualLastHandlerTime);
        receiver.setLastHandlerTime(lastHandlerTime == -1 ? System.currentTimeMillis() : lastHandlerTime);
        if (group == null)
            throw new NullPointerException("group is null");
        receiver.setGroup(group);
        return receiver;
    }
}
