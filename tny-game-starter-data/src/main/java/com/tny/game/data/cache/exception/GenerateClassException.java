package com.tny.game.data.cache.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/23 11:18 上午
 */
public class GenerateClassException extends CommonRuntimeException {

    public GenerateClassException() {
    }

    public GenerateClassException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public GenerateClassException(Throwable cause) {
        super(cause);
    }

    public GenerateClassException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
