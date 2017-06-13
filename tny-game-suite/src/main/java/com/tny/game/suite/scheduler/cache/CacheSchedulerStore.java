package com.tny.game.suite.scheduler.cache;

import com.tny.game.cache.async.AsyncCache;
import com.tny.game.scheduler.SchedulerBackup;
import com.tny.game.scheduler.SchedulerStore;
import com.tny.game.scheduler.TimeTaskScheduler;
import com.tny.game.suite.login.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER_CACHE, GAME})
public class CacheSchedulerStore implements SchedulerStore {

    @Autowired
    protected AsyncCache cache;

    private static String getKey(Object id) {
        return CacheSchedulerBackup.BACK_UP_KEY + id;
    }

    @Override
    public void save(TimeTaskScheduler timeTaskScheduler) {
        CacheSchedulerBackup cacheSchedulerBackup = new CacheSchedulerBackup(
                getKey(IDUtils.getSystemID()), timeTaskScheduler);
        this.cache.setObject(cacheSchedulerBackup);
    }

    @Override
    public SchedulerBackup load() {
        return this.cache.getObject(CacheSchedulerBackup.class, getKey(IDUtils.getSystemID()));
    }

}
