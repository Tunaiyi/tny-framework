package com.tny.game.suite.scheduler.spring;

import com.tny.game.suite.login.*;
import com.tny.game.suite.scheduler.*;
import com.tny.game.suite.scheduler.cache.*;
import com.tny.game.suite.scheduler.database.*;
import com.tny.game.suite.utils.*;
import org.springframework.context.annotation.*;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/9/10.
 */
@Configuration
public class SpringSchedulerConfiguration {

    @Bean
    @Profile({GAME})
    public TimeTaskSchedulerService gameSchedulerService() {
        return new CacheTimeTaskSchedulerService(IDAide.getSystemId());
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
