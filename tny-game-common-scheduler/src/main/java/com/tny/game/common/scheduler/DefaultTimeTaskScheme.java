package com.tny.game.common.scheduler;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/30 12:10 下午
 */
public class DefaultTimeTaskScheme implements TimeTaskScheme {

	private String cron;

	private List<String> tasks;

	@Override
	public String getCron() {
		return cron;
	}

	public DefaultTimeTaskScheme setCron(String cron) {
		this.cron = cron;
		return this;
	}

	@Override
	public List<String> getTasks() {
		return tasks;
	}

	public DefaultTimeTaskScheme setTasks(List<String> tasks) {
		this.tasks = tasks;
		return this;
	}

}
