package com.tny.game.actor.stage.exception;

public class StageTimeoutException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public StageTimeoutException(String message) {
		super(message);
	}

	public StageTimeoutException(Throwable cause) {
		super(cause);
	}

	public StageTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
