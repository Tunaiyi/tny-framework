package com.tny.game.net.transport.message.common;

import com.tny.game.net.transport.message.AbstractNetMessageHeader;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class CommonMessageHeader extends AbstractNetMessageHeader {

    private long id;

    private int code;

    private long time;

    private long toMessage;

    private int protocol;

    private Object head;

    @Override
    protected Object getHead() {
        return this.head;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public long getToMessage() {
        return this.toMessage;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    protected CommonMessageHeader setId(long id) {
        this.id = id;
        return this;
    }

    @Override
    protected CommonMessageHeader setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    protected CommonMessageHeader setTime(long time) {
        this.time = time;
        return this;
    }

    @Override
    protected CommonMessageHeader setToMessage(long toMessage) {
        this.toMessage = toMessage;
        return this;
    }

    @Override
    protected CommonMessageHeader setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    protected CommonMessageHeader setHead(Object head) {
        this.head = head;
        return this;
    }
}
