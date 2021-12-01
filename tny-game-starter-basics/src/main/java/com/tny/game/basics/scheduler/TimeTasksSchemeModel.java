package com.tny.game.basics.scheduler;

import com.tny.game.common.scheduler.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/30 12:04 下午
 */
public class TimeTasksSchemeModel {

	private List<TimeTask> timeTasks = new ArrayList<>();

	public List<TimeTask> getTimeTasks() {
		return timeTasks;
	}

	public TimeTasksSchemeModel setTimeTasks(List<TimeTask> timeTasks) {
		this.timeTasks = timeTasks;
		return this;
	}

}
