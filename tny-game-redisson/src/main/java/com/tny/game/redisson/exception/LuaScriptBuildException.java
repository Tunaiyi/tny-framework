package com.tny.game.redisson.exception;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/31 3:28 下午
 */
public class LuaScriptBuildException extends RuntimeException {

    public LuaScriptBuildException() {
    }

    public LuaScriptBuildException(String message) {
        super(message);
    }

    public LuaScriptBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public LuaScriptBuildException(Throwable cause) {
        super(cause);
    }

    public LuaScriptBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
