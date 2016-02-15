package com.tny.game.actor.local.reason;

/**
 * 创建失败的挂起原因
 * @author KGTny
 *
 */
public class CreationReason extends BaseSuspendReason {

	public static final CreationReason message = new CreationReason();

	private CreationReason() {
		super(SuspendReasonType.CREATION);
	}

	public static CreationReason instance() {
		return message;
	}

}
