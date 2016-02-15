package com.tny.game.actor.system;

import com.tny.game.actor.exception.ActorInitializationException;

/**
 * 创角Actor系统事件
 * @author KGTny
 *
 */
public class CreateSysMsg extends BaseSystemMessage {

	private static CreateSysMsg NONE_CAUSE_MESSAGE = new CreateSysMsg(null);

	/**
	 * 创建的原因
	 */
	private ActorInitializationException failCause;

	public static CreateSysMsg message() {
		return NONE_CAUSE_MESSAGE;
	}

	public static CreateSysMsg message(ActorInitializationException cause) {
		return new CreateSysMsg(cause);
	}

	CreateSysMsg(ActorInitializationException cause) {
		super(SystemMessageType.CREATE);
		this.failCause = cause;
	}

	/**
	 * 是否有创建的原因
	 * @return 有原因返回true, 返回false
	 */
	public boolean isFail() {
		return failCause != null;
	}

	/**
	 * 获取创建原因
	 * @return 创建原因
	 */
	public ActorInitializationException getFailCause() {
		return failCause;
	}

}
