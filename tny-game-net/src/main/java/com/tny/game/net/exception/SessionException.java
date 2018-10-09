package com.tny.game.net.exception;

import com.tny.game.common.utils.StringAide;

public class SessionException extends NetException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SessionException(String message) {
        super(message);
    }

    public SessionException(Throwable cause, String message) {
        super(message, cause);
    }

    public SessionException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public SessionException(String message, Object... params) {
        super(StringAide.format(message, params));
    }

}
