package com.tny.game.net.exception;

import com.tny.game.common.utils.*;

public class ConnectingException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ConnectingException(String message) {
        super(message);
    }

    public ConnectingException(Throwable cause, String message) {
        super(message, cause);
    }

    public ConnectingException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public ConnectingException(String message, Object... params) {
        super(StringAide.format(message, params));
    }

}
