package com.tny.game.net.command.processor;

import com.tny.game.net.command.task.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 11:27 上午
 */
public abstract class EndpointCommandTaskBoxProcessor<T extends CommandTaskBoxDriver> implements CommandTaskBoxProcessor,
		CommandTaskBoxDriverExecutor<T> {

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

	public void setBusSpinTimes(int busSpinTimes) {
		this.busSpinTimes = busSpinTimes;
	}

	public void setYieldTimes(int yieldTimes) {
		this.yieldTimes = yieldTimes;
	}

	@Override
	public int getBusSpinTimes() {
		return this.busSpinTimes;
	}

	@Override
	public int getYieldTimes() {
		return this.yieldTimes;
	}

}
