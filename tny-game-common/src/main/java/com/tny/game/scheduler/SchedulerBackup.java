package com.tny.game.scheduler;

import java.io.Serializable;
import java.util.Date;

public abstract class SchedulerBackup implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @uml.property name="stopTime"
     */
    protected long stopTime;

    /**
     * @uml.property name="timeTaskQueue"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    protected TimeTaskQueue timeTaskQueue;

    protected SchedulerBackup() {
    }

    protected SchedulerBackup(TimeTaskScheduler scheduler) {
        this.stopTime = scheduler.getStopTime();
        this.timeTaskQueue = scheduler.getTimeTaskQueue();
    }

    /**
     * @return
     * @uml.property name="stopTime"
     */
    protected long getStopTime() {
        return this.stopTime;
    }

    /**
     * @return
     * @uml.property name="timeTaskQueue"
     */
    protected TimeTaskQueue getTimeTaskQueue() {
        return this.timeTaskQueue;
    }

    @Override
    public String toString() {
        return "SchedulerBackup [stopTime=" + new Date(stopTime) + ", timeTaskQueueSize=" + timeTaskQueue.size() + "]";
    }

}
