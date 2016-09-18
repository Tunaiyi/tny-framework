package com.tny.game.suite.scheduler.spring;

import com.tny.game.scheduler.SchedulerStore;
import com.tny.game.scheduler.TimeTaskHandlerHolder;
import com.tny.game.scheduler.TimeTaskScheduler;
import com.tny.game.suite.utils.Configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component
@Profile({SCHEDULER_DB, GAME})
public class SpringTimeTaskScheduler extends TimeTaskScheduler {

    @Autowired
    public SpringTimeTaskScheduler(TimeTaskHandlerHolder handlerHolder, SchedulerStore store) throws Exception {
        super(Configs.SUITE_CONFIG.getStr(Configs.SUITE_TIME_TASK_PATH, Configs.TIME_TASK_MODEL_CONFIG_PATH), handlerHolder, store);
    }

}
