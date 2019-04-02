package com.tny.game.net.exception;

import com.tny.game.common.utils.StringAide;

public class EndpointException extends NetException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public EndpointException(String message) {
        super(message);
    }

    public EndpointException(Throwable cause, String message) {
        super(message, cause);
    }

    public EndpointException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public EndpointException(String message, Object... params) {
        super(StringAide.format(message, params));
    }

}
