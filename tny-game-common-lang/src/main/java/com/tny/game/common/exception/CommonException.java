package com.tny.game.common.exception;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 */
public class CommonException extends Exception {

    public CommonException() {
        super();
    }

    public CommonException(String message, Object... messageParams) {
        this(null, message, messageParams);
    }

    public CommonException(Throwable cause) {
        this(cause, "");
    }

    public CommonException(Throwable cause, String message, Object... messageParams) {
        super(format(message, messageParams), cause);
    }

}
