package com.tny.game.suite.scheduler;

import com.tny.game.common.scheduler.HandleType;
import com.tny.game.common.scheduler.TaskReceiver;
import com.tny.game.common.scheduler.TimeTaskHandler;
import com.tny.game.common.scheduler.TriggerContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class GameTimeTaskHandler implements TimeTaskHandler {

    private String name;

    private HandleType handleType;

    private Set<ReceiverType> receiverTypeSet = new HashSet<ReceiverType>();

    protected GameTimeTaskHandler(String name, HandleType handleType, ReceiverType... receiverTypes) {
        super();
        this.handleType = handleType;
        this.name = name;
        this.receiverTypeSet.addAll(Arrays.asList(receiverTypes));
    }

    @Override
    public HandleType getHandleType() {
        return this.handleType;
    }

    @Override
    public String getHandlerName() {
        return this.name;
    }

    /**
     * 处理 <br>
     *
     * @param receiver 任务接收器
     */
    @Override
    public void handle(TaskReceiver receiver, long executeTime, TriggerContext context) {
        this.doHandle(receiver, executeTime, context);
    }

    protected abstract void doHandle(TaskReceiver receiver, long triggerTime, TriggerContext context);

    @Override
    public boolean isHandleWith(Object group) {
        return this.receiverTypeSet.contains(group);
    }

}
