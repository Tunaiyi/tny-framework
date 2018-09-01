package com.tny.game.net.exception;

import com.tny.game.common.result.ResultCode;

public class DispatchTimeoutException extends DispatchException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DispatchTimeoutException(ResultCode code, String message) {
        this(code, message, null);
    }

    public DispatchTimeoutException(ResultCode code) {
        this(code, code.getMessage(), null);
    }

    public DispatchTimeoutException(ResultCode code, Throwable cause) {
        this(code, code.getMessage(), cause);
    }

    public DispatchTimeoutException(ResultCode code, String message, Throwable cause) {
        this(code, null, null, cause);
    }

    public DispatchTimeoutException(ResultCode code, Object body) {
        this(code, null, body, null);
    }

    public DispatchTimeoutException(ResultCode code, String message, Object body) {
        this(code, message, body, null);
    }

    public DispatchTimeoutException(ResultCode code, String message, Object body, Throwable cause) {
        super(code, message, body, cause);
    }

}
