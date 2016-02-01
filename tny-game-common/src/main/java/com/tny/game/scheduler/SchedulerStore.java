package com.tny.game.scheduler;

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
    public void save(TimeTaskScheduler timeTaskScheduler);

    /**
     * 读取存储方案
     *
     * @return
     */
    public SchedulerBackup load();


}
