package com.tny.game.actor.local.reason;

class BaseSuspendReason implements SuspendReason {

	private SuspendReasonType reasonType;

	protected BaseSuspendReason(SuspendReasonType reasonType) {
		super();
		this.reasonType = reasonType;
	}

	@Override
	public SuspendReasonType getReasonType() {
		return reasonType;
	}

}
