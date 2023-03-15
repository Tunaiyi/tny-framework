package com.tny.game.net.message;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/3/15 15:10
 **/
public enum NetworkWay {

    MESSAGE,

    SYSTEM,

    HEARTBEAT,

    ;

    private final String value;

    NetworkWay() {
        this.value = this.name().toLowerCase();
    }

    public String getValue() {
        return value;
    }
}
