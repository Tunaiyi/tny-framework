package com.tny.game.actor.stage.exception;

public class TaskInterruptedException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public TaskInterruptedException(String message) {
		super(message);
	}

	public TaskInterruptedException(Throwable cause) {
		super(cause);
	}

	public TaskInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}

}
