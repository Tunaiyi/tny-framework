package com.tny.game.data.mongodb.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 3:24 下午
 */
public class GetEntityIdException extends CommonRuntimeException {

	public GetEntityIdException() {
	}

	public GetEntityIdException(String message, Object... messageParams) {
		super(message, messageParams);
	}

	public GetEntityIdException(Throwable cause) {
		super(cause);
	}

	public GetEntityIdException(Throwable cause, String message, Object... messageParams) {
		super(cause, message, messageParams);
	}

}
