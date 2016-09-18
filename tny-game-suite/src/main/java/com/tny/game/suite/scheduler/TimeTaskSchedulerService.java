package com.tny.game.suite.scheduler;

import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;

public interface TimeTaskSchedulerService extends ServerPreStart {

    void checkPlayerTask(long playerID, ReceiverType receiverType);

    @Override
    default PerIniter getIniter() {
        return PerIniter.initer(this.getClass(), InitLevel.LEVEL_1);
    }

}
