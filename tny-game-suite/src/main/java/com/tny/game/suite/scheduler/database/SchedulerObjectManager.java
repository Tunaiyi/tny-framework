package com.tny.game.suite.scheduler.database;


import com.tny.game.common.scheduler.TaskReceiver;
import com.tny.game.suite.scheduler.GameTaskReceiver;
import com.tny.game.suite.scheduler.cache.CacheSchedulerBackup;
import com.tny.game.suite.scheduler.cache.CacheSchedulerBackupFormatter;
import com.tny.game.suite.scheduler.cache.TaskReceiverFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;

import static com.tny.game.suite.SuiteProfiles.SCHEDULER_DB;

@Component
@Profile({SCHEDULER_DB})
public class SchedulerObjectManager {

	private final static Logger logger = LoggerFactory.getLogger(SchedulerObjectManager.class);

	@Resource
	private SchedulerObjectDAO schedulerBackupDAO;

	@Resource
	private CacheSchedulerBackupFormatter backupFormatter;

	@Resource
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
