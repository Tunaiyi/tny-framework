package com.tny.game.net.netty4.processor.disruptor;

import com.tny.game.common.collection.map.access.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 1:35 上午
 */
public class DisruptorEndpointCommandTaskProcessorSetting {

	/**
	 * 间歇时间
	 */
	private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

	/**
	 * 时间
	 */
	private static final int DEFAULT_QUEUE_SIZE = 1024 * 1024;

	/**
	 * 间歇时间
	 */
	private static final int[] DEFAULT_COMMAND_TICK_TIME = {1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10};

	private boolean enable;

	/* 线程数 */
	private int threads = DEFAULT_THREADS;

	/* 线程数 */
	private int queueSize = DEFAULT_QUEUE_SIZE;

	/* 任务调度间隔序列 */
	private int[] commandTickTimeList = DEFAULT_COMMAND_TICK_TIME;

	/* ChildExecutor command 未完成, 延迟时间*/
	private DisruptorWaitStrategy waitStrategy = DisruptorWaitStrategy.BLOCKING;

	private ObjectMap waitStrategyProperty = new ObjectMap();

	public int getThreads() {
		return this.threads;
	}

	public DisruptorEndpointCommandTaskProcessorSetting setThreads(int threads) {
		this.threads = threads;
		return this;
	}

	public int getQueueSize() {
		return this.queueSize;
	}

	public DisruptorEndpointCommandTaskProcessorSetting setQueueSize(int queueSize) {
		this.queueSize = queueSize;
		return this;
	}

	public int[] getCommandTickTimeList() {
		return this.commandTickTimeList;
	}

	public DisruptorEndpointCommandTaskProcessorSetting setCommandTickTimeList(int[] commandTickTimeList) {
		if (ArrayUtils.isNotEmpty(commandTickTimeList)) {
			for (int value : commandTickTimeList) {
				Asserts.checkArgument(value > 0, "illegal argument commandTickTimeList [{}]", StringUtils.join(",", commandTickTimeList));
			}
			this.commandTickTimeList = commandTickTimeList;
		}
		return this;
	}

	public DisruptorWaitStrategy getWaitStrategy() {
		return this.waitStrategy;
	}

	public DisruptorEndpointCommandTaskProcessorSetting setWaitStrategy(DisruptorWaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
		return this;
	}

	public ObjectMap getWaitStrategyProperty() {
		return this.waitStrategyProperty;
	}

	public DisruptorEndpointCommandTaskProcessorSetting setWaitStrategyProperty(ObjectMap waitStrategyProperty) {
		this.waitStrategyProperty = waitStrategyProperty;
		return this;
	}

	public boolean isEnable() {
		return this.enable;
	}

	public DisruptorEndpointCommandTaskProcessorSetting setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

}