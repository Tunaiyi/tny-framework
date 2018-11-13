package com.tny.game.net.message.common;

import com.tny.game.net.message.AbstractNetMessageHeader;
import com.tny.game.net.transport.MessageContext;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class CommonMessageHeader extends AbstractNetMessageHeader {

    private long id;

    private int code;

    private long time;

    private long toMessage;

    private int protocol;

    private Object attachment;

    protected CommonMessageHeader() {
    }

    public CommonMessageHeader(long id, MessageContext<?> context) {
        super(context.getMode());
        this.id = id;
        this.protocol = context.getProtocolNumber();
        this.code = context.getCode().getCode();
        this.toMessage = context.getToMessage();
        this.time = System.currentTimeMillis();
        this.attachment = context.getAttachment();
    }

    @Override
    public Object getAttachment() {
        return this.attachment;
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
    public int getProtocolNumber() {
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

    CommonMessageHeader setAttachment(Object attachment) {
        this.attachment = attachment;
        return this;
    }
}
