package com.tny.game.net.exception;

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

    private Object body;

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
        this(code, message, null, cause);
    }

    public DispatchException(ResultCode code, Object body) {
        this(code, null, body, null);
    }

    public DispatchException(ResultCode code, String message, Object body) {
        this(code, message, body, null);
    }

    public DispatchException(ResultCode code, String message, Object body, Throwable cause) {
        super(message, cause);
        this.resultCode = code;
        this.body = body;
    }

    public ResultCode getResultCode() {
        return this.resultCode;
    }

    public Object getBody() {
        return body;
    }
}
