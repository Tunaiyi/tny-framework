package com.tny.game.suite.scheduler.spring;

import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.scheduler.TimeTaskSchedulerService;
import com.tny.game.suite.scheduler.cache.CacheTimeTaskSchedulerService;
import com.tny.game.suite.scheduler.database.DBTimeTaskSchedulerService;
import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/9/10.
 */
@Configuration
public class SpringSchedulerConfiguration {

    @Bean
    @Profile({GAME})
    public TimeTaskSchedulerService gameSchedulerService() {
        return new CacheTimeTaskSchedulerService(GameInfo.getSystemID());
    }

    @Bean
    @Profile({SCHEDULER_CACHE})
    public TimeTaskSchedulerService cacheSchedulerService() {
        int serverID = Configs.SERVICE_CONFIG.getInt(Configs.SERVER_ID, 0);
        return new CacheTimeTaskSchedulerService(serverID);
    }

    @Bean
    @Profile({SCHEDULER_DB})
    public TimeTaskSchedulerService databaseSchedulerService() {
        return new DBTimeTaskSchedulerService();
    }

}
