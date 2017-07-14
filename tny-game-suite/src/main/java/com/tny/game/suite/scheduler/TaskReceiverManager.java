package com.tny.game.suite.scheduler;

import com.tny.game.common.scheduler.TaskReceiver;

public interface TaskReceiverManager {

    TaskReceiver getSystemReceiver();

    TaskReceiver getPlayerReceiver(long playerID);

    boolean insert(TaskReceiver receiver);

    boolean save(TaskReceiver receiver);

}