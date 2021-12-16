package com.tny.game.net.exception;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;

import static com.tny.game.common.utils.StringAide.*;

public class NetCodecException extends CommandException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static NetCodecException causeEncodeFailed(Throwable cause, String message, Object... params) {
		return new NetCodecException(NetResultCode.ENCODE_FAILED, format(message, params), cause);
	}

	public static NetCodecException causeEncodeFailed(String message, Object... params) {
		return new NetCodecException(NetResultCode.ENCODE_FAILED, format(message, params));
	}

	public static NetCodecException causeEncodeError(Throwable cause, String message, Object... params) {
		return new NetCodecException(NetResultCode.ENCODE_ERROR, format(message, params), cause);
	}

	public static NetCodecException causeEncodeError(String message, Object... params) {
		return new NetCodecException(NetResultCode.ENCODE_ERROR, format(message, params));
	}

	public static NetCodecException causeDecodeError(Throwable cause, String message, Object... params) {
		return new NetCodecException(NetResultCode.DECODE_ERROR, format(message, params), cause);
	}

	public static NetCodecException causeDecodeError(String message, Object... params) {
		return new NetCodecException(NetResultCode.DECODE_ERROR, format(message, params));
	}

	public static NetCodecException causeDecodeFailed(Throwable cause, String message, Object... params) {
		return new NetCodecException(NetResultCode.DECODE_FAILED, format(message, params), cause);
	}

	public static NetCodecException causeDecodeFailed(String message, Object... params) {
		return new NetCodecException(NetResultCode.DECODE_FAILED, format(message, params));
	}

	public static NetCodecException causeTimeout(String message, Object... params) {
		return new NetCodecException(NetResultCode.PACKET_TIMEOUT, format(message, params));
	}

	public static NetCodecException causeVerify(String message, Object... params) {
		return new NetCodecException(NetResultCode.PACKET_VERIFY_FAILED, format(message, params));
	}

	//	private CodecException(ResultCode code) {
	//		super(code);
	//	}

	private NetCodecException(ResultCode code, String message) {
		super(code, message);
	}

	private NetCodecException(ResultCode code, String message, Throwable cause) {
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
