package com.tny.game.namespace.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:36
 **/
public class HashPartitionRegisteredException extends CommonRuntimeException {

    public HashPartitionRegisteredException() {
    }

    public HashPartitionRegisteredException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public HashPartitionRegisteredException(Throwable cause) {
        super(cause);
    }

    public HashPartitionRegisteredException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
