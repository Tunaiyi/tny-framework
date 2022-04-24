package com.tny.game.net.base;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 3:14 下午
 */
public enum ConnectCallbackStatus {

    CONNECTED(true),

    CONNECTING(false),

    EXCEPTION(false),

    ;

    private final boolean success;

    ConnectCallbackStatus(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
