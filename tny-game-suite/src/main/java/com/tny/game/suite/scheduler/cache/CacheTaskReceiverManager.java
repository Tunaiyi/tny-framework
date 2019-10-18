package com.tny.game.suite.scheduler.cache;

import com.tny.game.common.scheduler.TaskReceiver;
import com.tny.game.suite.base.GameCacheManager;
import com.tny.game.suite.login.IDAide;
import com.tny.game.suite.scheduler.GameTaskReceiver;
import com.tny.game.suite.scheduler.TaskReceiverManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER_CACHE, GAME})
public class CacheTaskReceiverManager extends GameCacheManager<TaskReceiver> implements TaskReceiverManager {

    protected CacheTaskReceiverManager() {
        super(GameTaskReceiver.class);
    }

    public TaskReceiver getSystemReceiver() {
        return this.get(IDAide.getSystemId());
    }

    public TaskReceiver getPlayerReceiver(long playerID) {
        return this.get(playerID);
    }

}