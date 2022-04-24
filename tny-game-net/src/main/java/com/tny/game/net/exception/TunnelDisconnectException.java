package com.tny.game.net.exception;

import com.tny.game.common.utils.*;

public class TunnelDisconnectException extends NetException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TunnelDisconnectException(String message) {
        super(message);
    }

    public TunnelDisconnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public TunnelDisconnectException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public TunnelDisconnectException(String message, Object... params) {
        super(StringAide.format(message, params));
    }

}
