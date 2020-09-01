package com.tny.game.common.exception;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 */
public class CommonRuntimeException extends RuntimeException {

    public CommonRuntimeException() {
        super();
    }

    public CommonRuntimeException(String message, Object... messageParams) {
        this(null, message, messageParams);
    }

    public CommonRuntimeException(Throwable cause) {
        this(cause, "");
    }

    public CommonRuntimeException(Throwable cause, String message, Object... messageParams) {
        super(format(message, messageParams), cause);
    }

}
