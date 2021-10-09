package com.tny.game.data.exception;

import com.tny.game.common.utils.*;

public class EntityCacheException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public EntityCacheException(String message) {
		super(message);
	}

	public EntityCacheException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityCacheException(Throwable cause, String message, Object... params) {
		super(StringAide.format(message, params), cause);
	}

	public EntityCacheException(String message, Object... params) {
		super(StringAide.format(message, params));
	}

}
