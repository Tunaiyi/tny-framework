package com.tny.game.suite.scheduler.cache;

import com.tny.game.cache.async.*;
import com.tny.game.common.scheduler.*;
import com.tny.game.suite.login.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER_CACHE, GAME})
public class CacheSchedulerStore implements SchedulerStore {

    @Resource
    protected AsyncCache cache;

    private static String getKey(Object id) {
        return CacheSchedulerBackup.BACK_UP_KEY + id;
    }

    @Override
    public void save(TimeTaskScheduler timeTaskScheduler) {
        CacheSchedulerBackup cacheSchedulerBackup = new CacheSchedulerBackup(
                getKey(IDAide.getSystemId()), timeTaskScheduler);
        this.cache.setObject(cacheSchedulerBackup);
    }

    @Override
    public SchedulerBackup load() {
        return this.cache.getObject(CacheSchedulerBackup.class, getKey(IDAide.getSystemId()));
    }

}
