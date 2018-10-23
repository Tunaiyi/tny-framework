package com.tny.game.net.exception;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.NetResultCode;

import static com.tny.game.common.utils.StringAide.*;

public class CodecException extends CommandException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static CodecException causeEncode(String message, Object... params) {
        return new CodecException(NetResultCode.ENCODE_ERROR, format(message, params));
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

    private CodecException(ResultCode code, String message) {
        super(code, message);
    }


}
