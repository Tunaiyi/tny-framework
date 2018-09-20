package com.tny.game.net.transport.message;

import com.tny.game.common.utils.*;

/**
 * Created by Kun Yang on 2018/7/23.
 */
public abstract class AbstractMessageHeader implements MessageHeader {

    private volatile MessageMode mode;

    protected AbstractMessageHeader() {
    }

    protected AbstractMessageHeader(MessageMode mode) {
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
