package com.tny.game.net.exception;

import com.tny.game.common.utils.StringAide;

public class TunnelException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TunnelException(String message) {
        super(message);
    }

    public TunnelException(Throwable cause, String message) {
        super(message, cause);
    }

    public TunnelException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public TunnelException(String message, Object... params) {
        super(StringAide.format(message, params));
    }


}
