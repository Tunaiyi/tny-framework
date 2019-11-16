package com.tny.game.suite.scheduler;

import com.tny.game.asyndb.annotation.*;
import com.tny.game.base.item.*;
import com.tny.game.cache.annotation.*;
import com.tny.game.common.scheduler.*;
import com.tny.game.suite.*;
import com.tny.game.suite.cache.spring.*;
import com.tny.game.suite.scheduler.cache.*;

import static com.tny.game.suite.SuiteProfiles.*;

@Persistent(synchronizerClass = DBCacheSynchronizer.class)
@ToCache(
        profiles = {SCHEDULER_CACHE, GAME},
        prefix = SuiteDBHead.CACHE_KEY_TASK_RECEIVER, triggers = TaskReceiverFormatter.class,
        cacheKeys = "playerId")
public class GameTaskReceiver extends TaskReceiver implements Owned {

    protected long playerId;

    protected GameTaskReceiver() {
    }

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    protected void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    protected void setGroup(ReceiverType group) {
        this.group = group;
    }

    public void setActualLastHandlerTime(long actualLastHandlerTime) {
        this.actualLastHandlerTime = actualLastHandlerTime;
    }

    protected void setLastHandlerTime(long lastHandlerTime) {
        this.lastHandlerTime = lastHandlerTime;
    }

}
