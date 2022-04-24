package com.tny.game.basics.scheduler;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.scheduler.*;

public interface TimeTaskService extends AppPrepareStart {

    void checkTask(long playerId, TaskReceiverType receiverType);

    @Override
    default PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.POST_SYSTEM_LEVEL_10);
    }

}
