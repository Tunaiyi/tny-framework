package com.tny.game.net.message;

/**
 * Created by Kun Yang on 2018/7/23.
 */
public abstract class AbstractNetMessageHead implements NetMessageHead {

    protected volatile MessageMode mode;

    protected AbstractNetMessageHead() {
    }

    protected AbstractNetMessageHead(MessageMode mode) {
        this.mode = mode;
    }

    @Override
    public MessageMode getMode() {
        return this.mode;
    }

}
