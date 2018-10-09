package com.tny.game.net.transport.message;

import com.tny.game.common.context.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.transport.Certificate;

public abstract class AbstractNetMessage<UID> implements NetMessage<UID> {

    private volatile transient Attributes attributes;

    private NetMessageHeader header;

    private Object body;

    protected Certificate<UID> certificate;

    protected AbstractNetMessage() {
    }

    protected AbstractNetMessage(NetMessageHeader header) {
        this.header = header;
    }

    protected AbstractNetMessage(Certificate<UID> certificate, NetMessageHeader header) {
        this.certificate = certificate;
        this.header = header;
    }

    @Override
    public UID getUserID() {
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
    public MessageHeader getHeader() {
        return header;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBody(ReferenceType<T> type) {
        return ObjectAide.converTo(getBody(), type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBody(Class<T> clazz) {
        return ObjectAide.converTo(this.getBody(), clazz);
    }

    @Override
    public Attributes attributes() {
        if (this.attributes != null)
            return this.attributes;
        synchronized (this) {
            if (this.attributes != null)
                return this.attributes;
            return this.attributes = ContextAttributes.create();
        }
    }

    protected AbstractNetMessage<UID> setCertificate(Certificate<UID> certificate) {
        this.certificate = certificate;
        return this;
    }

    protected Object getBody() {
        return this.body;
    }

    @Override
    public NetMessage<UID> setId(long messageID) {
        this.header.setId(messageID);
        return this;
    }

    @Override
    public NetMessage<UID> update(Certificate<UID> certificate) {
        this.setCertificate(certificate);
        return this;
    }

    // protected AbstractNetMessage<UID> setBody(Object body) {
    //     this.body = body;
    //     return this;
    // }

}
