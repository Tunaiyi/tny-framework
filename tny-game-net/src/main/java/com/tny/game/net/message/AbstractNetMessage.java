package com.tny.game.net.message;

import com.tny.game.common.context.*;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.utils.AppConstants;

public abstract class AbstractNetMessage<UID> extends AbstractMessageHeader implements NetMessage<UID> {

    protected volatile transient Attributes attributes;

    protected long sessionID;

    private MessageMode mode;

    private Object head;

    protected Tunnel<UID> tunnel;

    public AbstractNetMessage() {
    }

    @Override
    public UID getUserID() {
        Tunnel<UID> tunnel = this.tunnel;
        return tunnel == null ? null : tunnel.getUID();
    }

    @Override
    public long getSessionID() {
        return sessionID;
    }

    @Override
    public String getUserGroup() {
        Tunnel<UID> tunnel = this.tunnel;
        return tunnel == null ? AppConstants.DEFAULT_USER_GROUP : tunnel.getUserGroup();
    }

    @Override
    public boolean isLogin() {
        Tunnel<UID> tunnel = this.tunnel;
        return tunnel != null && tunnel.isLogin();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBody(ReferenceType<T> bodyClass) {
        return getBody(getClassType(bodyClass));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBody(Class<T> clazz) {
        return conver2Type(clazz, this.getBody());
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
    public MessageMode getMode() {
        if (mode == null)
            mode = MessageMode.getMode(this);
        return mode;
    }

    @Override
    public void sendBy(Tunnel<UID> tunnel) {
        setTunnel(tunnel);
    }

    protected AbstractNetMessage<UID> setTunnel(Tunnel<UID> tunnel) {
        this.tunnel = tunnel;
        this.sessionID = this.tunnel.getSession().getID();
        return this;
    }

    @Override
    protected Object getHead() {
        return head;
    }

    protected abstract Object getBody();

    protected abstract AbstractNetMessage<UID> setID(int ID);

    protected abstract AbstractNetMessage<UID> setProtocol(int protocol);

    protected abstract AbstractNetMessage<UID> setSign(String sign);

    protected abstract AbstractNetMessage<UID> setTime(long time);

    protected abstract AbstractNetMessage<UID> setBody(Object body);

    protected abstract AbstractNetMessage<UID> setCode(int code);

    protected abstract AbstractNetMessage<UID> setToMessage(int toMessage);

    protected AbstractNetMessage<UID> setHead(Object head) {
        this.head = head;
        return this;
    }

    protected AbstractNetMessage<UID> setSessionID(long sessionID) {
        this.sessionID = sessionID;
        return this;
    }

    // protected abstract AbstractNetMessage<UID> setSession(Session<UID> session);

}
