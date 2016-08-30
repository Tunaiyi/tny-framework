package com.tny.game.suite.scheduler;

import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.scheduler.TaskReceiver;
import com.tny.game.scheduler.TimeTaskScheduler;
import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER, GAME})
public class TimeTaskSchedulerService implements ServerPreStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(TimeTaskSchedulerService.class);

    @Autowired
    private TimeTaskScheduler scheduler;

    @Autowired
    private TaskReceiverManager taskReceiverManager;

    private void checkSystemTask() {
        TransactionManager.open();
        try {
            this.checkPlayerTask(GameInfo.getSystemID(), ReceiverType.SYSTEM);
            TransactionManager.close();
        } catch (Throwable e) {
            TransactionManager.rollback(e);
            LOGGER.error("checkSystemTask", e);
        }
    }

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

    @Override
    public PerIniter getIniter() {
        return PerIniter.initer(this.getClass(), InitLevel.LEVEL_1);
    }

}
