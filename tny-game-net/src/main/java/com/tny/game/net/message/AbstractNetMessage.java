package com.tny.game.net.message;

import com.tny.game.common.context.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.transport.*;

public abstract class AbstractNetMessage<UID> extends AttributesHolder implements NetMessage<UID> {

    private NetMessageHead head;

    private Object body;

    private Object tail;

    protected Certificate<UID> certificate;

    protected AbstractNetMessage() {
    }

    protected AbstractNetMessage(NetMessageHead head) {
        this.head = head;
    }

    protected AbstractNetMessage(Certificate<UID> certificate, NetMessageHead head, Object body, Object tail) {
        this.certificate = certificate;
        this.head = head;
        this.body = body;
        this.tail = tail;
    }

    @Override
    public UID getUserId() {
        return certificate.getUserId();
    }

    @Override
    public String getUserType() {
        return certificate.getUserType();
    }

    @Override
    public boolean isLogin() {
        return certificate.isAutherized();
    }

    @Override
    public MessageHead getHead() {
        return head;
    }

    @Override
    public <T> T getBody(ReferenceType<T> type) {
        return ObjectAide.converTo(this.body, type);
    }

    @Override
    public <T> T getBody(Class<T> clazz) {
        return ObjectAide.converTo(this.body, clazz);
    }

    @Override
    public boolean existBody() {
        return this.body != null;
    }

    @Override
    public boolean existTail() {
        return this.tail != null;
    }

    @Override
    public <T> T getTail(Class<T> clazz) {
        return ObjectAide.converTo(this.tail, clazz);
    }

    @Override
    public <T> T getTail(ReferenceType<T> clazz) {
        return ObjectAide.converTo(this.tail, clazz);
    }

    protected AbstractNetMessage<UID> setCertificate(Certificate<UID> certificate) {
        this.certificate = certificate;
        return this;
    }

    // @Override
    // public Object getBody() {
    //     return this.body;
    // }

    // @Override
    // public NetMessage<UID> setId(long messageID) {
    //     this.head.setId(messageID);
    //     return this;
    // }

    @Override
    public NetMessage<UID> update(Certificate<UID> certificate) {
        this.setCertificate(certificate);
        return this;
    }

    protected AbstractNetMessage<UID> setBody(Object body) {
        this.body = body;
        return this;
    }

}
