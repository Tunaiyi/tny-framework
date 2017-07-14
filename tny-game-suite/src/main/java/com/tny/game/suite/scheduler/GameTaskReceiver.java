package com.tny.game.suite.scheduler;

import com.tny.game.asyndb.annotation.Persistent;
import com.tny.game.base.item.Identifiable;
import com.tny.game.cache.annotation.ToCache;
import com.tny.game.common.scheduler.TaskReceiver;
import com.tny.game.suite.SuiteDBHead;
import com.tny.game.suite.cache.spring.DBCacheSynchronizer;
import com.tny.game.suite.scheduler.cache.TaskReceiverFormatter;

@Persistent(synchronizerClass = DBCacheSynchronizer.class)
@ToCache(prefix = SuiteDBHead.CACHE_KEY_TASK_RECEIVER, triggers = TaskReceiverFormatter.class,
        cacheKeys = "playerID")
public class GameTaskReceiver extends TaskReceiver implements Identifiable {

    protected long playerID;

    protected GameTaskReceiver() {
    }

    @Override
    public long getPlayerID() {
        return this.playerID;
    }

    protected void setPlayerID(long playerID) {
        this.playerID = playerID;
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
