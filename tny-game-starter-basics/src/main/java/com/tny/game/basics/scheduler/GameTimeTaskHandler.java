package com.tny.game.basics.scheduler;

import com.tny.game.common.scheduler.*;

import java.util.*;

public abstract class GameTimeTaskHandler implements TimeTaskHandler {

	private String name;

	private HandleType handleType;

	private Set<TaskReceiverType> receiverTypeSet = new HashSet<>();

	protected GameTimeTaskHandler(String name, HandleType handleType, TaskReceiverType... receiverTypes) {
		super();
		this.handleType = handleType;
		this.name = name;
		this.receiverTypeSet.addAll(Arrays.asList(receiverTypes));
	}

	@Override
	public HandleType getHandleType() {
		return this.handleType;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * 处理 <br>
	 *
	 * @param receiver 任务接收器
	 */
	@Override
	public void handle(TaskReceiver receiver, long executeTime, TriggerContext context) {
		if (this.receiverTypeSet.contains(receiver.getType())) {
			this.doHandle(receiver, executeTime, context);
		}
	}

	protected abstract void doHandle(TaskReceiver receiver, long executeTime, TriggerContext context);

	@Override
	public boolean isHandleWith(TaskReceiverType group) {
		return this.receiverTypeSet.contains(group);
	}

}
