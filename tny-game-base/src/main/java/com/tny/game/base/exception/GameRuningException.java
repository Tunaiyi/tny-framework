package com.tny.game.base.exception;

import com.tny.game.common.result.ResultCode;

public class GameRuningException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private ResultCode resultCode;

    /**
     * 附加对象
     */
    private Object data;

    public GameRuningException(ResultCode code, Object... messages) {
        super(format(null, code, messages));
        this.resultCode = code;
        this.data = null;
    }

    public GameRuningException(Object data, ResultCode code, Object... messages) {
        super(format(data, code, messages));
        this.resultCode = code;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    protected static String format(Object data, ResultCode code, Object... messages) {
        String exMessage = "\n###CODE : " + code.getCode() + "\n###MESSAGE : " + code.getMessage() + "\n###NFO : ";
        if (data != null)
            exMessage += "\n   ---  " + data;
        for (Object message : messages)
            exMessage += "\n   ---  " + message;
        return exMessage;
    }

}
