package com.tny.game.net.exception;

import com.tny.game.common.utils.*;

public class TunnelCloseException extends NetException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TunnelCloseException(String message) {
        super(message);
    }

    public TunnelCloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TunnelCloseException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public TunnelCloseException(String message, Object... params) {
        super(StringAide.format(message, params));
    }


}
