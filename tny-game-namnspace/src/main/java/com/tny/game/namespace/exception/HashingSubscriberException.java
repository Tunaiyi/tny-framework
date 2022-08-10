package com.tny.game.namespace.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class HashingSubscriberException extends CommonRuntimeException {

    public HashingSubscriberException() {
    }

    public HashingSubscriberException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public HashingSubscriberException(Throwable cause) {
        super(cause);
    }

    public HashingSubscriberException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
