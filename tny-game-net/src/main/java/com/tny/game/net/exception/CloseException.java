package com.tny.game.net.exception;

import com.tny.game.common.utils.*;

public class CloseException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CloseException(String message) {
        super(message);
    }

    public CloseException(Throwable cause, String message) {
        super(message, cause);
    }

    public CloseException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public CloseException(String message, Object... params) {
        super(StringAide.format(message, params));
    }

}
