package com.tny.game.codec.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 11:58 上午
 */
public class ObjectCodecException extends CommonRuntimeException {

    public ObjectCodecException() {
    }

    public ObjectCodecException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public ObjectCodecException(Throwable cause) {
        super(cause);
    }

    public ObjectCodecException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
