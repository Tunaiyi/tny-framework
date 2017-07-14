package com.tny.game.suite.scheduler.cache;

import com.tny.game.asyndb.annotation.Persistent;
import com.tny.game.cache.annotation.ToCache;
import com.tny.game.common.scheduler.SchedulerBackup;
import com.tny.game.common.scheduler.TimeTask;
import com.tny.game.common.scheduler.TimeTaskQueue;
import com.tny.game.common.scheduler.TimeTaskScheduler;
import com.tny.game.suite.SuiteDBHead;
import com.tny.game.suite.cache.spring.DBCacheSynchronizer;

import java.util.Collection;

@Persistent(asyn = false, synchronizerClass = DBCacheSynchronizer.class)
@ToCache(prefix = SuiteDBHead.CACHE_KEY_GAME_SERVER, triggers = CacheSchedulerBackupFormatter.class,
        cacheKeys = {"key"})
public class CacheSchedulerBackup extends SchedulerBackup {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String BACK_UP_KEY = "BACKUP";

    private Object key = BACK_UP_KEY;

    public Object getKey() {
        return this.key;
    }

    protected CacheSchedulerBackup() {
    }

    public CacheSchedulerBackup(Object key, TimeTaskScheduler scheduler) {
        super(scheduler);
        this.key = key;
    }

    @Override
    protected long getStopTime() {
        return this.stopTime;
    }

    protected Collection<TimeTask> getTimeTaskList() {
        return this.timeTaskQueue.getTimeTaskList();
    }

    protected void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    protected void setTimeTaskList(Collection<TimeTask> taskCollection) {
        this.timeTaskQueue = new TimeTaskQueue();
        for (TimeTask task : taskCollection) {
            this.timeTaskQueue.put(task);
        }
    }

}
