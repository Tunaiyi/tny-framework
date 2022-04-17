package com.tny.game.codec.typeprotobuf.exception;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/14 16:58
 **/
public class TypeProtobufSchemeException extends RuntimeException {

	public TypeProtobufSchemeException() {
	}

	public TypeProtobufSchemeException(String message) {
		super(message);
	}

	public TypeProtobufSchemeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TypeProtobufSchemeException(Throwable cause) {
		super(cause);
	}

	public TypeProtobufSchemeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
