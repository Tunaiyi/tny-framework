package com.tny.game.suite.scheduler;

import com.tny.game.common.lifecycle.LifecycleLevel;
import com.tny.game.common.lifecycle.PrepareStarter;
import com.tny.game.common.lifecycle.AppPrepareStart;

public interface TimeTaskSchedulerService extends AppPrepareStart {

    void checkPlayerTask(long playerID, ReceiverType receiverType);

    @Override
    default PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.POST_SYSTEM_LEVEL_10);
    }

}
