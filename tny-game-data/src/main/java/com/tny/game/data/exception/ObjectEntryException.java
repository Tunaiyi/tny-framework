package com.tny.game.data.exception;

/**
 * <p>
 */
public class ObjectEntryException extends DataException {

    public ObjectEntryException(String message) {
        super(message);
    }

    public ObjectEntryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectEntryException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

    public ObjectEntryException(String message, Object... params) {
        super(message, params);
    }

}
