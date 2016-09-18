package com.tny.game.suite.scheduler.database;

import com.tny.game.scheduler.SchedulerBackup;
import com.tny.game.scheduler.TimeTask;
import com.tny.game.scheduler.TimeTaskQueue;
import com.tny.game.scheduler.TimeTaskScheduler;

import java.util.Collection;

public class DBSchedulerBackup extends SchedulerBackup {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected DBSchedulerBackup() {
    }

    protected DBSchedulerBackup(TimeTaskScheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected long getStopTime() {
        return this.stopTime;
    }

    protected Collection<TimeTask> getTimeTaskList() {
        return this.timeTaskQueue.getTimeTaskList();
    }

    protected void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    protected void setTimeTaskList(Collection<TimeTask> taskCollection) {
        this.timeTaskQueue = new TimeTaskQueue();
        for (TimeTask task : taskCollection) {
            this.timeTaskQueue.put(task);
        }
    }

}
