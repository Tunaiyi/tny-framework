package com.tny.game.net.message;

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

    protected abstract Object getAttachment();

    @Override
    public boolean isHasAttachment() {
        return this.getAttachment() != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttachment(ReferenceType<T> type) {
        return ObjectAide.converTo(getAttachment(), type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttachment(Class<T> clazz) {
        return ObjectAide.converTo(this.getAttachment(), clazz);
    }

}
