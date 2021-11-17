package com.tny.game.net.command.processor;

import com.tny.game.net.endpoint.task.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 11:27 上午
 */
public abstract class EndpointCommandTaskBoxProcessor<T extends CommandTaskBoxDriver> implements CommandTaskBoxProcessor {

	private int busSpinTimes = 10;

	private int yieldTimes = 10;

	@Override
	public void submit(CommandTaskBox box) {
		CommandTaskBoxDriver driver = box.getAttachment(this);
		if (driver == null) {
			// 创角任务触发器
			driver = box.setAttachmentIfNull(this, () -> createDriver(box));
		}
		driver.trySubmit();// 尝试提交
	}

	protected abstract T createDriver(CommandTaskBox box);

	protected abstract void process(T processor);

	protected abstract void schedule(T processor);

	public EndpointCommandTaskBoxProcessor<T> setBusSpinTimes(int busSpinTimes) {
		this.busSpinTimes = busSpinTimes;
		return this;
	}

	public EndpointCommandTaskBoxProcessor<T> setYieldTimes(int yieldTimes) {
		this.yieldTimes = yieldTimes;
		return this;
	}

	public int getBusSpinTimes() {
		return this.busSpinTimes;
	}

	public int getYieldTimes() {
		return this.yieldTimes;
	}

}
