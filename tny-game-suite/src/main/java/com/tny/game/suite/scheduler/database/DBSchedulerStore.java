package com.tny.game.suite.scheduler.database;

import com.tny.game.common.scheduler.SchedulerBackup;
import com.tny.game.common.scheduler.SchedulerStore;
import com.tny.game.common.scheduler.TimeTaskScheduler;
import com.tny.game.suite.scheduler.cache.CacheSchedulerBackup;
import com.tny.game.suite.utils.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER_DB})
public class DBSchedulerStore implements SchedulerStore {

    public static final Logger LOGGER = LoggerFactory.getLogger(DBSchedulerStore.class);

    @Resource
    private SchedulerObjectManager schedulerObjectManager;

    private static String getKey(Object id) {
        return CacheSchedulerBackup.BACK_UP_KEY + id;
    }

    @Override
    public void save(TimeTaskScheduler timeTaskScheduler) {
        try {
            int serverID = Configs.SERVICE_CONFIG.getInt(Configs.SERVER_ID, 0);
            CacheSchedulerBackup cacheSchedulerBackup = new CacheSchedulerBackup(
                    getKey(serverID), timeTaskScheduler);
            schedulerObjectManager.saveSchedulerBackup(cacheSchedulerBackup);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public SchedulerBackup load() {
        return schedulerObjectManager.getSchedulerBackup();
    }

}
