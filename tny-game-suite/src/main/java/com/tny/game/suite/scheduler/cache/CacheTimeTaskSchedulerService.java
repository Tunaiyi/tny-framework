package com.tny.game.suite.scheduler.cache;

import com.tny.game.scheduler.TaskReceiver;
import com.tny.game.scheduler.TimeTaskScheduler;
import com.tny.game.suite.scheduler.ReceiverType;
import com.tny.game.suite.scheduler.TaskReceiverBuilder;
import com.tny.game.suite.scheduler.TaskReceiverManager;
import com.tny.game.suite.scheduler.TimeTaskSchedulerService;
import com.tny.game.suite.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheTimeTaskSchedulerService implements TimeTaskSchedulerService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CacheTimeTaskSchedulerService.class);

    @Autowired
    private TimeTaskScheduler scheduler;

    @Autowired
    private TaskReceiverManager taskReceiverManager;

    private long serverID;

    public CacheTimeTaskSchedulerService(long serverID) {
        this.serverID = serverID;
    }

    private void checkSystemTask() {
        TransactionManager.open();
        try {
            this.checkPlayerTask(serverID, ReceiverType.SYSTEM);
            TransactionManager.close();
        } catch (Throwable e) {
            TransactionManager.rollback(e);
            LOGGER.error("checkSystemTask", e);
        }
    }

    @Override
    public void checkPlayerTask(long playerID, ReceiverType receiverType) {
        TaskReceiver dbReceiver = this.taskReceiverManager.getPlayerReceiver(playerID);
        final TaskReceiver receiver;
        if (dbReceiver == null) {
            receiver = TaskReceiverBuilder.create()
                    .setGroup(receiverType)
                    .setPlayerID(playerID)
                    .build();
            this.taskReceiverManager.insert(receiver);
        } else {
            receiver = dbReceiver;
        }
        this.scheduler.schedule(receiver);
        this.taskReceiverManager.save(receiver);
    }

    @Override
    public void initialize() throws Exception {
        this.scheduler.addListener(timeTask -> this.checkSystemTask());
        this.scheduler.start();
    }

}