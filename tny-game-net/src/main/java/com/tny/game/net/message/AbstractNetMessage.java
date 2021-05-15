package com.tny.game.net.message;

import com.tny.game.common.context.*;
import com.tny.game.common.type.*;
import com.tny.game.common.utils.*;

public abstract class AbstractNetMessage extends AttributesHolder implements NetMessage {

    private NetMessageHead head;

    private Object body;

    protected AbstractNetMessage() {
    }

    protected AbstractNetMessage(NetMessageHead head) {
        this.head = head;
    }

    protected AbstractNetMessage(NetMessageHead head, Object body) {
        this.head = head;
        this.body = body;
    }

    @Override
    public MessageHead getHead() {
        return this.head;
    }

    @Override
    public <T> T getBody(ReferenceType<T> type) {
        return ObjectAide.convertTo(this.body, type);
    }

    @Override
    public <T> T getBody(Class<T> clazz) {
        return ObjectAide.convertTo(this.body, clazz);
    }

    @Override
    public boolean existBody() {
        return this.body != null;
    }

    protected AbstractNetMessage setBody(Object body) {
        this.body = body;
        return this;
    }

}
