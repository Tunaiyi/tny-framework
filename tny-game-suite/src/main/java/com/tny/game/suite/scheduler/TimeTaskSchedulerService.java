package com.tny.game.suite.scheduler;

import com.tny.game.lifecycle.LifecycleLevel;
import com.tny.game.lifecycle.PrepareStarter;
import com.tny.game.lifecycle.ServerPrepareStart;

public interface TimeTaskSchedulerService extends ServerPrepareStart {

    void checkPlayerTask(long playerID, ReceiverType receiverType);

    @Override
    default PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.LEVEL_1);
    }

}
