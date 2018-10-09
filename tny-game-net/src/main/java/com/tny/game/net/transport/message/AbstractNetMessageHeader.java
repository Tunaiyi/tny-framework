package com.tny.game.net.transport.message;

import com.tny.game.common.utils.*;

/**
 * Created by Kun Yang on 2018/7/23.
 */
public abstract class AbstractNetMessageHeader implements NetMessageHeader {

    private volatile MessageMode mode;

    protected AbstractNetMessageHeader() {
    }

    protected AbstractNetMessageHeader(MessageMode mode) {
        this.mode = mode;
    }

    @Override
    public MessageMode getMode() {
        if (mode != null)
            return mode;
        synchronized (this) {
            if (mode != null)
                return mode;
            if (mode == null)
                mode = MessageMode.getMode(this);
        }
        return mode;
    }


    protected abstract Object getHead();

    @Override
    public boolean isHasHead() {
        return this.getHead() != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getHead(ReferenceType<T> type) {
        return ObjectAide.converTo(getHead(), type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getHead(Class<T> clazz) {
        return ObjectAide.converTo(this.getHead(), clazz);
    }

}
