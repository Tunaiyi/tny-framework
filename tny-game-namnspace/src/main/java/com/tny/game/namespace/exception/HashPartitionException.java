package com.tny.game.namespace.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class HashPartitionException extends CommonRuntimeException {

    public HashPartitionException() {
    }

    public HashPartitionException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public HashPartitionException(Throwable cause) {
        super(cause);
    }

    public HashPartitionException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
