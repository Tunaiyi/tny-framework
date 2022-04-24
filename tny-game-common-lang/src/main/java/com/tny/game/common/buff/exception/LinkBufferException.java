package com.tny.game.common.buff.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/12 3:59 下午
 */
public class LinkBufferException extends CommonRuntimeException {

    public LinkBufferException() {
    }

    public LinkBufferException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public LinkBufferException(Throwable cause) {
        super(cause);
    }

    public LinkBufferException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
