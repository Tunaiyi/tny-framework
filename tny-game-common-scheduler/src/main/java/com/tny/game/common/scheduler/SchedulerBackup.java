package com.tny.game.common.scheduler;

import java.io.Serializable;
import java.util.*;

public abstract class SchedulerBackup implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @uml.property name="stopTime"
	 */
	private long stopTime;

	/**
	 * @uml.property name="timeTaskQueue"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Collection<TimeTask> timeTaskQueue;

	protected SchedulerBackup() {
	}

	protected SchedulerBackup(TimeTaskScheduler scheduler) {
		this.stopTime = scheduler.getStopTime();
		TimeTaskQueue queue = scheduler.getTimeTaskQueue();
		this.timeTaskQueue = queue.getTimeTaskList();
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
	protected List<TimeTask> getTimeTaskQueue() {
		return new ArrayList<>(this.timeTaskQueue);
	}

	@Override
	public String toString() {
		return "SchedulerBackup [stopTime=" + new Date(this.stopTime) + ", timeTaskQueueSize=" + this.timeTaskQueue.size() + "]";
	}

}
