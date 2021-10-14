package com.tny.game.data.mongodb.exception;

/**
 * 动态生成类失败
 * <p>
 */
public class GenerateConverterException extends RuntimeException {

	public GenerateConverterException() {
	}

	public GenerateConverterException(String message) {
		super(message);
	}

	public GenerateConverterException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerateConverterException(Throwable cause) {
		super(cause);
	}

	public GenerateConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
