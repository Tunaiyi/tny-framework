package com.tny.game.suite.scheduler;

import com.tny.game.asyndb.annotation.Persistent;
import com.tny.game.base.item.Identifier;
import com.tny.game.cache.annotation.ToCache;
import com.tny.game.common.scheduler.TaskReceiver;
import com.tny.game.suite.SuiteDBHead;
import com.tny.game.suite.cache.spring.DBCacheSynchronizer;
import com.tny.game.suite.scheduler.cache.TaskReceiverFormatter;

import static com.tny.game.suite.SuiteProfiles.GAME;
import static com.tny.game.suite.SuiteProfiles.SCHEDULER_CACHE;

@Persistent(synchronizerClass = DBCacheSynchronizer.class)
@ToCache(
        profiles = {SCHEDULER_CACHE, GAME},
        prefix = SuiteDBHead.CACHE_KEY_TASK_RECEIVER, triggers = TaskReceiverFormatter.class,
        cacheKeys = "playerId")
public class GameTaskReceiver extends TaskReceiver implements Identifier {

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
