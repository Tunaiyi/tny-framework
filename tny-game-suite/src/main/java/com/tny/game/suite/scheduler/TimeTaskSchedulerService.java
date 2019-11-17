package com.tny.game.suite.scheduler;

import com.tny.game.common.lifecycle.*;

public interface TimeTaskSchedulerService extends AppPrepareStart {

    void checkPlayerTask(long playerId, ReceiverType receiverType);

    @Override
    default PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.POST_SYSTEM_LEVEL_10);
    }

}
