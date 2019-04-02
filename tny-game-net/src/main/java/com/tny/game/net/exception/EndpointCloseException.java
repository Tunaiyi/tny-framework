package com.tny.game.net.exception;

import com.tny.game.common.utils.*;

public class EndpointCloseException extends EndpointException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public EndpointCloseException(String message) {
        super(message);
    }

    public EndpointCloseException(Throwable cause, String message) {
        super(message, cause);
    }

    public EndpointCloseException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public EndpointCloseException(String message, Object... params) {
        super(StringAide.format(message, params));
    }

}
