package com.tny.game.net.dispatcher.exception;

import com.tny.game.common.result.ResultCode;

public class DispatchException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private ResultCode resultCode;

    public DispatchException(ResultCode code, String message) {
        this(code, message, null);
    }

    public DispatchException(ResultCode code) {
        this(code, code.getMessage(), null);
    }

    public DispatchException(ResultCode code, Throwable cause) {
        this(code, code.getMessage(), cause);
    }

    public DispatchException(ResultCode code, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = code;
    }

    public ResultCode getResultCode() {
        return this.resultCode;
    }

}
