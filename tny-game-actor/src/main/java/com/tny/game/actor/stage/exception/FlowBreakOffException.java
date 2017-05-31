package com.tny.game.actor.stage.exception;

public class FlowBreakOffException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FlowBreakOffException() {
		super();
	}

	public FlowBreakOffException(String message) {
		super(message);
	}

	public FlowBreakOffException(Throwable cause) {
		super(cause);
	}

	public FlowBreakOffException(String message, Throwable cause) {
		super(message, cause);
	}

}
