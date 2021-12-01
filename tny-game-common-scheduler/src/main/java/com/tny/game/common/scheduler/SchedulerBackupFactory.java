package com.tny.game.common.scheduler;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/29 9:02 下午
 */
public interface SchedulerBackupFactory {

	SchedulerBackup create(TimeTaskScheduler scheduler);

}
