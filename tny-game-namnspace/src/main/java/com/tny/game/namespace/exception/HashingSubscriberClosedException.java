package com.tny.game.namespace.exception;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class HashingSubscriberClosedException extends HashingSubscriberException {

    public HashingSubscriberClosedException() {
    }

    public HashingSubscriberClosedException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public HashingSubscriberClosedException(Throwable cause) {
        super(cause);
    }

    public HashingSubscriberClosedException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
