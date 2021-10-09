package com.tny.game.data.cache.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 1:35 下午
 */
public class GetCacheIdException extends CommonRuntimeException {

	public GetCacheIdException() {
	}

	public GetCacheIdException(String message, Object... messageParams) {
		super(message, messageParams);
	}

	public GetCacheIdException(Throwable cause) {
		super(cause);
	}

	public GetCacheIdException(Throwable cause, String message, Object... messageParams) {
		super(cause, message, messageParams);
	}

}
