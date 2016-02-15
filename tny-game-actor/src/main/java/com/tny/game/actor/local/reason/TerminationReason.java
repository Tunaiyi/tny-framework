package com.tny.game.actor.local.reason;

/**
 * 终止Actor的挂起原因
 * @author KGTny
 *
 */
public class TerminationReason extends BaseSuspendReason {

	public static final TerminationReason message = new TerminationReason();

	private TerminationReason() {
		super(SuspendReasonType.TERMINATION);
	}

	public static TerminationReason instance() {
		return message;
	}

}
