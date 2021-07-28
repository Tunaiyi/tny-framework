package com.tny.game.redisson.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/16 1:57 上午
 */
public class LuaScripRunException extends CommonRuntimeException {

    public LuaScripRunException() {
    }

    public LuaScripRunException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public LuaScripRunException(Throwable cause) {
        super(cause);
    }

    public LuaScripRunException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
