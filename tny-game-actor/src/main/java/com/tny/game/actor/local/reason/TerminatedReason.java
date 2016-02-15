package com.tny.game.actor.local.reason;

/**
 * 终止Actor的挂起原因
 * @author KGTny
 *
 */
public class TerminatedReason extends BaseSuspendReason {

	public static final TerminatedReason message = new TerminatedReason();

	private TerminatedReason() {
		super(SuspendReasonType.TERMINATED);
	}

	public static TerminatedReason instance() {
		return message;
	}

}
