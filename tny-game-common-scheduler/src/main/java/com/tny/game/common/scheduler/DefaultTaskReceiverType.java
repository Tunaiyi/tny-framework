package com.tny.game.common.scheduler;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/30 4:06 下午
 */
public enum DefaultTaskReceiverType implements TaskReceiverType {

	SYSTEM(1),

	PLAYER(2),

	//
	;

	int id;

	DefaultTaskReceiverType(int id) {
		this.id = id;
	}

	@Override
	public int id() {
		return id;
	}
}
