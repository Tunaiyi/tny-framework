package com.tny.game.namespace.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class HashRingException extends CommonRuntimeException {

    public HashRingException() {
    }

    public HashRingException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public HashRingException(Throwable cause) {
        super(cause);
    }

    public HashRingException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
