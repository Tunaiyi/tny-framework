package com.tny.game.suite.scheduler;

import com.tny.game.scheduler.TaskReceiver;
import com.tny.game.suite.base.GameCacheManager;
import com.tny.game.suite.core.GameInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"suite.scheduler", "suite.all"})
public class TaskReceiverManager extends GameCacheManager<TaskReceiver> {

    protected TaskReceiverManager() {
        super(GameTaskReceiver.class);
    }

    protected TaskReceiver getSystemReceiver() {
        return this.get(GameInfo.getSystemID());
    }

    protected TaskReceiver getPlayerReceiver(long playerID) {
        return this.get(playerID);
    }

}