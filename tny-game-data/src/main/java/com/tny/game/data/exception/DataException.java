package com.tny.game.data.exception;

import com.tny.game.common.utils.*;

public class DataException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public DataException(String message, Object... params) {
        super(StringAide.format(message, params));
    }

}
