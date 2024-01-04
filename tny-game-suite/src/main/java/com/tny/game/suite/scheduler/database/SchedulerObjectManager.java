package com.tny.game.suite.scheduler.database;

import com.tny.game.common.scheduler.*;
import com.tny.game.suite.scheduler.*;
import com.tny.game.suite.scheduler.cache.*;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER_DB})
public class SchedulerObjectManager {

    private final static Logger logger = LoggerFactory.getLogger(SchedulerObjectManager.class);

    @Autowired
    private SchedulerObjectDAO schedulerBackupDAO;

    @Autowired
    private CacheSchedulerBackupFormatter backupFormatter;

    @Autowired
    private TaskReceiverFormatter receiverFormatter;

    public void saveSchedulerBackup(CacheSchedulerBackup cacheSchedulerBackup) {
        byte[] data = (byte[]) this.backupFormatter.format2Save(null, cacheSchedulerBackup);
        if (data != null) {
            try {
                this.schedulerBackupDAO.set(SchedulerObjectDAO.BACK_UP_KEY, new SerialBlob(data));
            } catch (SQLException e) {
                logger.error("", e);
            }
        }
    }

    public CacheSchedulerBackup getSchedulerBackup() {
        Blob blob = this.schedulerBackupDAO.get(SchedulerObjectDAO.BACK_UP_KEY);
        if (blob != null) {
            try {
                byte[] data = blob.getBytes(1, (int) blob.length());
                return (CacheSchedulerBackup) this.backupFormatter.format2Load(null, data);
            } catch (SQLException e) {
                logger.error("", e);
            }
        }
        return null;
    }

    public void saveTaskReceiver(GameTaskReceiver taskReceiver) {
        byte[] data = (byte[]) this.receiverFormatter.format2Save(null, taskReceiver);
        if (data != null) {
            try {
                this.schedulerBackupDAO.set(SchedulerObjectDAO.RECEIVER_KEY, new SerialBlob(data));
            } catch (SQLException e) {
                logger.error("", e);
            }
        }
    }

    public TaskReceiver getTaskReceiver() {
        Blob blob = this.schedulerBackupDAO.get(SchedulerObjectDAO.RECEIVER_KEY);
        if (blob != null) {
            try {
                byte[] data = blob.getBytes(1, (int) blob.length());
                return (TaskReceiver) this.receiverFormatter.format2Load(null, data);
            } catch (SQLException e) {
                logger.error("", e);
            }
        }
        return null;
    }

}
