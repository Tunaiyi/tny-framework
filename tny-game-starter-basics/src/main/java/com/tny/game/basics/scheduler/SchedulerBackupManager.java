package com.tny.game.basics.scheduler;

import com.tny.game.common.scheduler.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/29 9:07 下午
 */
public interface SchedulerBackupManager {

	SchedulerBackup getBackup(long id);

	boolean saveBackup(SchedulerBackup receiver);

}
