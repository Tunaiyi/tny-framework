package com.tny.game.common.reflect.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/22 12:19 下午
 */
public class ReflectException extends CommonRuntimeException {

    public ReflectException() {
    }

    public ReflectException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }

    public ReflectException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
