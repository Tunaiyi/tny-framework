package com.tny.game.suite.scheduler.database;

import com.tny.game.common.scheduler.*;
import com.tny.game.suite.scheduler.*;
import org.slf4j.*;

import javax.annotation.Resource;

public class DBTimeTaskSchedulerService implements TimeTaskSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBTimeTaskSchedulerService.class);

    @Autowired
    private TimeTaskScheduler scheduler;

    @Autowired
    private SchedulerObjectManager schedulerObjectManager;

    private GameTaskReceiver taskReceiver;

    private void checkSystemTask() {
        try {
            if (this.taskReceiver == null) {
                TaskReceiver dbReceiver = this.schedulerObjectManager.getTaskReceiver();
                if (dbReceiver == null) {
                    this.taskReceiver = (GameTaskReceiver) TaskReceiverBuilder.create()
                            .setGroup(ReceiverType.SYSTEM)
                            .setPlayerId(0)
                            .build();
                    this.schedulerObjectManager.saveTaskReceiver(this.taskReceiver);
                } else {
                    this.taskReceiver = (GameTaskReceiver) dbReceiver;
                }
            }
            this.scheduler.schedule(this.taskReceiver);
            this.schedulerObjectManager.saveTaskReceiver(this.taskReceiver);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void checkPlayerTask(long playerId, ReceiverType receiverType) {
        checkSystemTask();
    }

    @Override
    public void prepareStart() throws Exception {
        if (!this.scheduler.isStart()) {
            this.scheduler.addListener(timeTask -> checkSystemTask());
            this.scheduler.start();
        }
    }

}
