package com.tny.game.data.exception;

import com.tny.game.common.utils.*;

public class StoreOperateException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public StoreOperateException(String message) {
        super(message);
    }

    public StoreOperateException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreOperateException(Throwable cause, String message, Object... params) {
        super(StringAide.format(message, params), cause);
    }

    public StoreOperateException(String message, Object... params) {
        super(StringAide.format(message, params));
    }


}
