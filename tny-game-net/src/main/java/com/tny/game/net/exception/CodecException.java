package com.tny.game.net.exception;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;

import static com.tny.game.common.utils.StringAide.*;

public class CodecException extends CommandException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static CodecException causeEncode(Throwable cause, String message, Object... params) {
		return new CodecException(NetResultCode.ENCODE_FAILED, format(message, params), cause);
	}

	public static CodecException causeDecode(Throwable cause, String message, Object... params) {
		return new CodecException(NetResultCode.DECODE_ERROR, format(message, params), cause);
	}

	public static CodecException causeEncode(String message, Object... params) {
		return new CodecException(NetResultCode.ENCODE_FAILED, format(message, params));
	}

	public static CodecException causeDecode(String message, Object... params) {
		return new CodecException(NetResultCode.DECODE_ERROR, format(message, params));
	}

	public static CodecException causeTimeout(String message, Object... params) {
		return new CodecException(NetResultCode.PACKET_TIMEOUT, format(message, params));
	}

	public static CodecException causeVerify(String message, Object... params) {
		return new CodecException(NetResultCode.PACKET_VERIFY_FAILED, format(message, params));
	}

	//	private CodecException(ResultCode code) {
	//		super(code);
	//	}

	private CodecException(ResultCode code, String message) {
		super(code, message);
	}

	private CodecException(ResultCode code, String message, Throwable cause) {
		super(code, message, cause);
	}

	//	private CodecException(ResultCode code, Throwable cause) {
	//		super(code, cause);
	//	}
	//
	//
	//	private CodecException(ResultCode code, Object body) {
	//		super(code, body);
	//	}
	//
	//	private CodecException(ResultCode code, String message, Object body) {
	//		super(code, message, body);
	//	}
	//
	//	private CodecException(ResultCode code, String message, Object body, Throwable cause) {
	//		super(code, message, body, cause);
	//	}

}
