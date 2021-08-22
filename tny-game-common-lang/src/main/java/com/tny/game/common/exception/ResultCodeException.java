package com.tny.game.common.exception;

import com.tny.game.common.result.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 */
public class ResultCodeException extends CommonException {

	private final ResultCode code;

	public ResultCodeException(ResultCode code) {
		super();
		this.code = code;
	}

	public ResultCodeException(ResultCode code, String message, Object... messageParams) {
		this(code, null, message, messageParams);
	}

	public ResultCodeException(ResultCode code, Throwable cause) {
		this(code, cause, "");
	}

	public ResultCodeException(ResultCode code, Throwable cause, String message, Object... messageParams) {
		super(format(message, messageParams), cause);
		this.code = code;
	}

	public ResultCode getCode() {
		return code;
	}

}
