package com.tny.game.common.scheduler;

/**
 * 调度存储接口
 *
 * @author Kun.y
 */
public interface SchedulerStore {

    /**
     * 保存存储方案
     *
     * @param timeTaskScheduler
     */
    void store(TimeTaskScheduler timeTaskScheduler);

    /**
     * 读取存储方案
     *
     * @return
     */
    SchedulerBackup restore();

}
