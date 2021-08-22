package com.tny.game.common.exception;

import com.tny.game.common.result.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 */
public class ResultCodeRuntimeException extends CommonRuntimeException {

	private final ResultCode code;

	public ResultCodeRuntimeException(ResultCode code) {
		super();
		this.code = code;
	}

	public ResultCodeRuntimeException(ResultCode code, String message, Object... messageParams) {
		this(code, null, message, messageParams);
	}

	public ResultCodeRuntimeException(ResultCode code, Throwable cause) {
		this(code, cause, "");
	}

	public ResultCodeRuntimeException(ResultCode code, Throwable cause, String message, Object... messageParams) {
		super(format(message, messageParams), cause);
		this.code = code;
	}

	public ResultCode getCode() {
		return code;
	}

}
