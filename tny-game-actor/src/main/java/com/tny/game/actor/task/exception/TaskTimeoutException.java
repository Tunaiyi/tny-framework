package com.tny.game.actor.task.exception;

public class TaskTimeoutException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public TaskTimeoutException(String message) {
		super(message);
	}

	public TaskTimeoutException(Throwable cause) {
		super(cause);
	}

	public TaskTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
