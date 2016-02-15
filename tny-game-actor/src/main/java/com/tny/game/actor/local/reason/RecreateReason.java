package com.tny.game.actor.local.reason;

/**
 * 重新创建Actor失败的挂起原因
 * @author KGTny
 *
 */
public class RecreateReason extends BaseSuspendReason {

	private Throwable cause;

	public RecreateReason(Throwable cause) {
		super(SuspendReasonType.RECREATE);
		this.cause = cause;
	}

	/**
	 * @return 返回失败原因
	 */
	public Throwable getCause() {
		return cause;
	}

	public static RecreateReason instance(Throwable cause) {
		return new RecreateReason(cause);
	}

}
