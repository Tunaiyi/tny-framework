package com.tny.game.net.transport.message.common;

import com.tny.game.net.transport.message.*;

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

    protected CommonMessageHeader() {
    }

    public CommonMessageHeader(long id, MessageSubject subject) {
        super(subject.getMode());
        this.id = id;
        this.protocol = subject.getNumber();
        this.code = subject.getCode().getCode();
        this.toMessage = subject.getToMessage();
        this.time = System.currentTimeMillis();
        this.head = subject.getHeader();
    }

    @Override
    public Object getHead() {
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
    public int getNumber() {
        return this.protocol;
    }

    @Override
    public CommonMessageHeader setId(long id) {
        this.id = id;
        return this;
    }

    CommonMessageHeader setCode(int code) {
        this.code = code;
        return this;
    }

    CommonMessageHeader setTime(long time) {
        this.time = time;
        return this;
    }

    CommonMessageHeader setToMessage(long toMessage) {
        this.toMessage = toMessage;
        return this;
    }

    CommonMessageHeader setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    CommonMessageHeader setHead(Object head) {
        this.head = head;
        return this;
    }
}
