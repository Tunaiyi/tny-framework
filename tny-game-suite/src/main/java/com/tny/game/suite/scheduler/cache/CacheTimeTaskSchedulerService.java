package com.tny.game.suite.scheduler.cache;

import com.tny.game.boot.transaction.*;
import com.tny.game.common.scheduler.*;
import com.tny.game.suite.scheduler.*;
import org.slf4j.*;

import javax.annotation.Resource;

public class CacheTimeTaskSchedulerService implements TimeTaskSchedulerService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CacheTimeTaskSchedulerService.class);

    @Resource
    private TimeTaskScheduler scheduler;

    @Resource
    private TaskReceiverManager taskReceiverManager;

    private long serverID;

    public CacheTimeTaskSchedulerService(long serverID) {
        this.serverID = serverID;
    }

    private void checkSystemTask() {
        TransactionManager.open();
        try {
            this.checkPlayerTask(this.serverID, ReceiverType.SYSTEM);
            TransactionManager.close();
        } catch (Throwable e) {
            TransactionManager.rollback(e);
            LOGGER.error("checkSystemTask", e);
        }
    }

    @Override
    public void checkPlayerTask(long playerId, ReceiverType receiverType) {
        TaskReceiver dbReceiver = this.taskReceiverManager.getPlayerReceiver(playerId);
        final TaskReceiver receiver;
        if (dbReceiver == null) {
            receiver = TaskReceiverBuilder.create()
                    .setGroup(receiverType)
                    .setPlayerId(playerId)
                    .build();
            this.taskReceiverManager.insert(receiver);
        } else {
            receiver = dbReceiver;
        }

        if (receiver.getGroup() == receiverType) {
            this.scheduler.schedule(receiver);
            this.taskReceiverManager.save(receiver);
        }
    }

    @Override
    public void prepareStart() throws Exception {
        this.scheduler.addListener(timeTask -> this.checkSystemTask());
        this.scheduler.start();
    }

}
