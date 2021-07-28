package com.tny.game.boot.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/26 4:38 下午
 */
public class BootApplicationException extends CommonRuntimeException {

    public BootApplicationException() {
    }

    public BootApplicationException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public BootApplicationException(Throwable cause) {
        super(cause);
    }

    public BootApplicationException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
