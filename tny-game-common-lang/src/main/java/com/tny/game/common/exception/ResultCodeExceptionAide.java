package com.tny.game.common.exception;

import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 8:25 下午
 */
public class ResultCodeExceptionAide {

	private ResultCodeExceptionAide() {
	}

	public static ResultCode codeOf(Throwable cause) {
		return codeOf(cause, null);
	}

	public static ResultCode codeOf(Throwable cause, ResultCode defaultCode) {
		if (cause instanceof ResultCodeException) {
			return ((ResultCodeException)cause).getCode();
		} else if (cause instanceof ResultCodeRuntimeException) {
			return ((ResultCodeRuntimeException)cause).getCode();
		}
		return defaultCode;
	}

}
