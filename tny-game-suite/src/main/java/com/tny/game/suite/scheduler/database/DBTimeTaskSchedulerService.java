package com.tny.game.suite.scheduler.database;

import com.tny.game.common.scheduler.TaskReceiver;
import com.tny.game.common.scheduler.TimeTaskScheduler;
import com.tny.game.suite.scheduler.GameTaskReceiver;
import com.tny.game.suite.scheduler.ReceiverType;
import com.tny.game.suite.scheduler.TaskReceiverBuilder;
import com.tny.game.suite.scheduler.TimeTaskSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
                            .setPlayerID(0)
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
    public void checkPlayerTask(long playerID, ReceiverType receiverType) {
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
