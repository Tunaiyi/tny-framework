package com.tny.game.net.transport.message.protoex;

import com.google.common.base.MoreObjects;
import com.tny.game.net.transport.message.*;
import com.tny.game.protoex.annotations.*;

import java.util.Objects;

@ProtoEx(ProtoExMessageCoder.MESSAGE_HEAD_ID)
public class ProtoExMessageHeader extends AbstractNetMessageHeader {

    @ProtoExField(1)
    private long id;

    @ProtoExField(2)
    private int protocol = -1;

    @ProtoExField(3)
    private int code;

    @ProtoExField(4)
    private long toMessage;

    @ProtoExField(5)
    private long time = -1;

    @ProtoExField(6)
    @Packed(false)
    @ProtoExConf(typeEncode = TypeEncode.EXPLICIT)
    private Object head;

    public ProtoExMessageHeader() {
    }

    public ProtoExMessageHeader(long id, MessageSubject subject) {
        super(subject.getMode());
        this.id = id;
        this.protocol = subject.getNumber();
        this.code = subject.getCode().getCode();
        this.toMessage = subject.getToMessage();
        this.time = System.currentTimeMillis();
        this.head = subject.getHeader();
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
    public long getToMessage() {
        return toMessage;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    public int getNumber() {
        return this.protocol;
    }

    @Override
    protected Object getHead() {
        return head;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("protocol", protocol)
                .add("code", code)
                .add("toMessage", toMessage)
                .add("time", time)
                .add("head", head)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProtoExMessageHeader)) return false;
        ProtoExMessageHeader that = (ProtoExMessageHeader) o;
        return getId() == that.getId() &&
                this.getId() == this.getId() &&
                getCode() == that.getCode() &&
                getToMessage() == that.getToMessage() &&
                getTime() == that.getTime() &&
                Objects.equals(getHead(), that.getHead());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), this.getId(), getCode(), getToMessage(), getTime(), getHead());
    }

    @Override
    public ProtoExMessageHeader setId(long id) {
        this.id = id;
        return this;
    }

    ProtoExMessageHeader setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    ProtoExMessageHeader setCode(int code) {
        this.code = code;
        return this;
    }

    ProtoExMessageHeader setToMessage(long toMessage) {
        this.toMessage = toMessage;
        return this;
    }

    ProtoExMessageHeader setTime(long time) {
        this.time = time;
        return this;
    }

    ProtoExMessageHeader setHead(Object head) {
        this.head = head;
        return this;
    }
}

