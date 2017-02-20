package com.tny.game.net.dispatcher;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public enum MessageOrderType {

    SEND(true),

    RESEND(false);

    private boolean cache;

    MessageOrderType(boolean cache) {
        this.cache = cache;
    }

    public boolean isCache() {
        return cache;
    }
}
