package com.tny.game.actor.system;

/**
 * 恢复Actor系统事件
 * @author KGTny
 *
 */
public class ResumeSysMsg extends BaseSystemMessage {

	public static final ResumeSysMsg NONE_CAUSE_MESSAGE = new ResumeSysMsg(null);

	/**
	 * 恢复原因
	 */
	private Throwable cause;

	public static ResumeSysMsg message() {
		return NONE_CAUSE_MESSAGE;
	}

	public static ResumeSysMsg message(Throwable cause) {
		return new ResumeSysMsg(cause);
	}

	private ResumeSysMsg(Throwable cause) {
		super(SystemMessageType.RESUME);
		this.cause = cause;
	}

	/***
	 * @return 返回恢复原因
	 */
	public Throwable getCause() {
		return cause;
	}

}
