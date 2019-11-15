package com.tny.game.net.exception;

import com.tny.game.common.result.*;

public class CommandTimeoutException extends CommandException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CommandTimeoutException(ResultCode code, String message) {
        this(code, message, null);
    }

    public CommandTimeoutException(ResultCode code) {
        this(code, code.getMessage(), null);
    }

    public CommandTimeoutException(ResultCode code, Throwable cause) {
        this(code, code.getMessage(), cause);
    }

    public CommandTimeoutException(ResultCode code, String message, Throwable cause) {
        this(code, null, null, cause);
    }

    public CommandTimeoutException(ResultCode code, Object body) {
        this(code, null, body, null);
    }

    public CommandTimeoutException(ResultCode code, String message, Object body) {
        this(code, message, body, null);
    }

    public CommandTimeoutException(ResultCode code, String message, Object body, Throwable cause) {
        super(code, message, body, cause);
    }

}
