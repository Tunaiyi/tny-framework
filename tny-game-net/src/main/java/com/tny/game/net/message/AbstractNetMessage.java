package com.tny.game.net.message;

import com.tny.game.common.context.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.utils.SessionConstants;

public abstract class AbstractNetMessage<UID> implements NetMessage<UID> {

    private volatile transient Attributes attributes;

    private long sessionID;

    private MessageHeader header;

    private Object body;

    protected Tunnel<UID> tunnel;

    public AbstractNetMessage() {
    }

    @Override
    public UID getUserID() {
        Tunnel<UID> tunnel = this.tunnel;
        return tunnel == null ? null : tunnel.getUid();
    }

    @Override
    public MessageMode getMode() {
        return this.header.getMode();
    }

    @Override
    public long getSessionID() {
        return sessionID;
    }

    @Override
    public String getUserGroup() {
        Tunnel<UID> tunnel = this.tunnel;
        return tunnel == null ? SessionConstants.DEFAULT_USER_GROUP : tunnel.getUserGroup();
    }

    @Override
    public boolean isLogin() {
        Tunnel<UID> tunnel = this.tunnel;
        return tunnel != null && tunnel.isLogin();
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

    @Override
    public void sendBy(Tunnel<UID> tunnel) {
        setTunnel(tunnel);
    }

    protected AbstractNetMessage<UID> setTunnel(Tunnel<UID> tunnel) {
        this.tunnel = tunnel;
        this.sessionID = this.tunnel.getSession().getId();
        return this;
    }

    protected  Object getBody() {
        return this.body;
    }

    protected AbstractNetMessage<UID> setBody(Object body) {
        this.body = body;
        return this;
    }

    protected AbstractNetMessage<UID> setHeader(MessageHeader header) {
        this.header = header;
        return this;
    }

    protected AbstractNetMessage<UID> setSessionID(long sessionID) {
        this.sessionID = sessionID;
        return this;
    }

    // protected abstract AbstractNetMessage<UID> setSession(Session<UID> session);

}
