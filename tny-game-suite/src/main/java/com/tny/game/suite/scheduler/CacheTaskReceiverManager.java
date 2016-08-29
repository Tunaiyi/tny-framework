package com.tny.game.suite.scheduler;

import com.tny.game.scheduler.TaskReceiver;
import com.tny.game.suite.base.GameCacheManager;
import com.tny.game.suite.core.GameInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER, GAME})
public class CacheTaskReceiverManager extends GameCacheManager<TaskReceiver> implements TaskReceiverManager {

    protected CacheTaskReceiverManager() {
        super(GameTaskReceiver.class);
    }

    public TaskReceiver getSystemReceiver() {
        return this.get(GameInfo.getSystemID());
    }

    public TaskReceiver getPlayerReceiver(long playerID) {
        return this.get(playerID);
    }

}