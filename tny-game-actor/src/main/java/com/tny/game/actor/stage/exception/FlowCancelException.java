package com.tny.game.actor.stage.exception;

public class FlowCancelException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FlowCancelException() {
		super();
	}

	public FlowCancelException(String message) {
		super(message);
	}

	public FlowCancelException(Throwable cause) {
		super(cause);
	}

	public FlowCancelException(String message, Throwable cause) {
		super(message, cause);
	}

}
