package com.tny.game.net.relay.exception;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 4:14 下午
 */
public class LinkClosedException extends RuntimeException {

	public LinkClosedException() {
	}

	public LinkClosedException(String message) {
		super(message);
	}

	public LinkClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public LinkClosedException(Throwable cause) {
		super(cause);
	}

	public LinkClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
