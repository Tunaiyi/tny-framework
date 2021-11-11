package com.tny.game.net.exception;

import com.tny.game.common.utils.*;

public class ConnectedException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ConnectedException(String message) {
		super(message);
	}

	public ConnectedException(Throwable cause, String message) {
		super(message, cause);
	}

	public ConnectedException(Throwable cause, String message, Object... params) {
		super(StringAide.format(message, params), cause);
	}

	public ConnectedException(String message, Object... params) {
		super(StringAide.format(message, params));
	}

}
