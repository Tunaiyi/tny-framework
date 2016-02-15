package com.tny.game.actor.system;


/**
 * 重新创建Actor系统事件
 * @author KGTny
 *
 */
public class RecreateSysMsg extends BaseSystemMessage {

	public static final RecreateSysMsg NONE_CAUSE_MESSAGE = new RecreateSysMsg(null);

	/**
	 * 重新创建原因
	 */
	private Throwable cause;

	public static RecreateSysMsg message() {
		return NONE_CAUSE_MESSAGE;
	}

	public static RecreateSysMsg message(Throwable cause) {
		return new RecreateSysMsg(cause);
	}

	private RecreateSysMsg(Throwable cause) {
		super(SystemMessageType.RECREATE);
		this.cause = cause;
	}

	/***
	 * 获取重新创建原因
	 * @return 返回重新创建原因
	 */
	public Throwable getCause() {
		return cause;
	}

}
