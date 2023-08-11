package com.tny.game.suite.scheduler.database;

import com.tny.game.common.scheduler.*;
import com.tny.game.suite.core.*;
import com.tny.game.suite.scheduler.cache.*;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER_DB})
public class DBSchedulerStore implements SchedulerStore {

    public static final Logger LOGGER = LoggerFactory.getLogger(DBSchedulerStore.class);

    @Autowired
    private SchedulerObjectManager schedulerObjectManager;

    private static String getKey(Object id) {
        return CacheSchedulerBackup.BACK_UP_KEY + id;
    }

    @Override
    public void save(TimeTaskScheduler timeTaskScheduler) {
        try {
            int serverID = GameInfo.info().getServerId();//Configs.SERVICE_CONFIG.getInt(Configs.SERVER_ID, 0);
            CacheSchedulerBackup cacheSchedulerBackup = new CacheSchedulerBackup(
                    getKey(serverID), timeTaskScheduler);
            this.schedulerObjectManager.saveSchedulerBackup(cacheSchedulerBackup);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public SchedulerBackup load() {
        return this.schedulerObjectManager.getSchedulerBackup();
    }

}
