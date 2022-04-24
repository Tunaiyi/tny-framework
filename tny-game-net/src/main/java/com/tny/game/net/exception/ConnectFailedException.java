package com.tny.game.net.exception;

import com.tny.game.common.utils.*;

public class ConnectFailedException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ConnectFailedException(String message) {
        super(message);
    }

    public ConnectFailedException(Throwable cause, String message) {
        super(message, cause);
    }

    public ConnectFailedException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public ConnectFailedException(String message, Object... params) {
        super(StringAide.format(message, params));
    }

}
