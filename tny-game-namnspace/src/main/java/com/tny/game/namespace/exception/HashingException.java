package com.tny.game.namespace.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class HashingException extends CommonRuntimeException {

    public HashingException() {
    }

    public HashingException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public HashingException(Throwable cause) {
        super(cause);
    }

    public HashingException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
