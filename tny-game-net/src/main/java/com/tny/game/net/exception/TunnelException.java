package com.tny.game.net.exception;

import com.tny.game.common.utils.StringAide;

public class TunnelException extends NetException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TunnelException(String message) {
        super(message);
    }

    public TunnelException(String message, Throwable cause) {
        super(message, cause);
    }

    public TunnelException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public TunnelException(String message, Object... params) {
        super(StringAide.format(message, params));
    }


}
