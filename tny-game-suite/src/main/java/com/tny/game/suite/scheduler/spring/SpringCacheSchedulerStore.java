package com.tny.game.suite.scheduler.spring;

import com.tny.game.cache.async.AsyncCache;
import com.tny.game.scheduler.SchedulerBackup;
import com.tny.game.scheduler.SchedulerStore;
import com.tny.game.scheduler.TimeTaskScheduler;
import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.scheduler.CacheSchedulerBackup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("schedulerStore")
@Profile({"suite.scheduler.cache_store"})
public class SpringCacheSchedulerStore implements SchedulerStore {

    @Autowired
    protected AsyncCache cache;

    private static String getKey(Object id) {
        return CacheSchedulerBackup.BACK_UP_KEY + id;
    }

    @Override
    public void save(TimeTaskScheduler timeTaskScheduler) {
        CacheSchedulerBackup cacheSchedulerBackup = new CacheSchedulerBackup(
                getKey(GameInfo.getSystemID()), timeTaskScheduler);
        this.cache.setObject(cacheSchedulerBackup);
    }

    @Override
    public SchedulerBackup load() {
        return this.cache.getObject(CacheSchedulerBackup.class, getKey(GameInfo.getSystemID()));
    }

}
