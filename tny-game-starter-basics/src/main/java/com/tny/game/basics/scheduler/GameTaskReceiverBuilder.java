package com.tny.game.basics.scheduler;

import com.tny.game.common.scheduler.*;

public abstract class GameTaskReceiverBuilder {

	/**
	 * 接收者ID
	 *
	 * @uml.property name="receicerId"
	 */
	private long playerId;

	/**
	 * 任务组
	 *
	 * @uml.property name="group"
	 */
	private TaskReceiverType type;

	/**
	 * 最后处理任务的时间
	 *
	 * @uml.property name="lastHandlerTime"
	 */
	private long lastHandlerTime = -1L;

	/**
	 * 最后一次实际处理时间
	 */
	private long actualLastHandlerTime = -1L;

	protected GameTaskReceiverBuilder() {
		super();
	}

	public GameTaskReceiverBuilder setPlayerId(long playerId) {
		this.playerId = playerId;
		return this;
	}

	public GameTaskReceiverBuilder setType(TaskReceiverType type) {
		this.type = type;
		return this;
	}

	public GameTaskReceiverBuilder setLastHandlerTime(long lastHandlerTime) {
		this.lastHandlerTime = lastHandlerTime;
		return this;
	}

	public GameTaskReceiverBuilder setActualLastHandlerTime(long actualLastHandlerTime) {
		this.actualLastHandlerTime = actualLastHandlerTime;
		return this;
	}

	public GameTaskReceiver build() {
		GameTaskReceiver receiver = newReceiver();
		receiver.setPlayerId(this.playerId);
		receiver.setActualLastHandlerTime(this.actualLastHandlerTime == -1 ? System.currentTimeMillis() : this.actualLastHandlerTime);
		receiver.setLastHandlerTime(this.lastHandlerTime == -1 ? System.currentTimeMillis() : this.lastHandlerTime);
		if (this.type == null) {
			throw new NullPointerException("group is null");
		}
		receiver.setType(this.type);
		return receiver;
	}

	protected GameTaskReceiver newReceiver() {
		return new GameTaskReceiver();
	}

}
