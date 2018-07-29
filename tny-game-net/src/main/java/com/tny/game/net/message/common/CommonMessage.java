package com.tny.game.net.message.common;

import com.tny.game.net.message.AbstractNetMessage;

public class CommonMessage<UID> extends AbstractNetMessage<UID> {

    private static final long serialVersionUID = 1L;

    private int ID;

    private int protocol = -1;

    private int code;

    private int toMessage;

    private String sign;

    private long time = -1;

    private Object body;

    public CommonMessage() {
        super();
    }

    @Override
    public int getID() {
        return this.ID;
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
    public CommonMessage<UID> setID(int ID) {
        this.ID = ID;
        return this;
    }

    @Override
    public CommonMessage<UID> setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public CommonMessage<UID> setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public CommonMessage<UID> setBody(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public CommonMessage<UID> setToMessage(int toMessage) {
        this.toMessage = toMessage;
        return this;
    }

    @Override
    protected AbstractNetMessage<UID> setSessionID(long sessionID) {
        this.sessionID = sessionID;
        return this;
    }

    @Override
    public CommonMessage<UID> setSign(String sign) {
        this.sign = sign;
        return this;
    }

    @Override
    public CommonMessage<UID> setTime(long time) {
        this.time = time;
        return this;
    }

    @Override
    protected CommonMessage<UID> setHead(Object head) {
        super.setHead(head);
        return this;
    }

    @Override
    public String toString() {
        return "DefaultMessage [ID=" + this.ID + ", protocol=" + this.protocol + ", sign=" + this.sign
                + ", time=" + this.time + ", paramList="
                + this.body + "]";
    }

}

