package com.tny.game.net.message.defalut;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.AbstractNetMessage;
import com.tny.game.net.session.Session;

public class DefaultMessage<UID> extends AbstractNetMessage<UID> {

    private static final long serialVersionUID = 1L;

    private int ID;

    private int protocol = -1;

    private int code;

    private int toMessage;

    private String sign;

    private long time = -1;

    private Object body;

    private String group;

    private UID uid;

    public DefaultMessage() {
    }

    protected DefaultMessage(Session<UID> session) {
        this.setSession(session);
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public UID getUserID() {
        return uid;
    }

    @Override
    public String getUserGroup() {
        return group;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public int getToMessage() {
        return toMessage;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public String getSign() {
        return this.sign;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    protected Object getBody() {
        return body;
    }

    @Override
    public DefaultMessage<UID> setID(int ID) {
        this.ID = ID;
        return this;
    }

    @Override
    public DefaultMessage<UID> setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    public DefaultMessage<UID> setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public DefaultMessage<UID> setBody(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public DefaultMessage<UID> setToMessage(int toMessage) {
        this.toMessage = toMessage;
        return this;
    }

    @Override
    public DefaultMessage<UID> setSign(String sign) {
        this.sign = sign;
        return this;
    }

    @Override
    public DefaultMessage<UID> setTime(long time) {
        this.time = time;
        return this;
    }

    @Override
    protected DefaultMessage<UID> setSession(Session<UID> session) {
        LoginCertificate<UID> certificate = session.getCertificate();
        this.uid = certificate.getUserID();
        this.group = certificate.getUserGroup();
        return this;
    }

    public DefaultMessage<UID> setGroup(String group) {
        this.group = group;
        return this;
    }

    public DefaultMessage<UID> setUid(UID uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public String toString() {
        return "ProtoRequest [ID=" + this.ID + ", protocol=" + this.protocol + ", sign=" + this.sign
                + ", time=" + this.time + ", paramList="
                + this.body + "]";
    }

    @Override
    public void register(Session<UID> session) {
        this.setSession(session);
    }
}

