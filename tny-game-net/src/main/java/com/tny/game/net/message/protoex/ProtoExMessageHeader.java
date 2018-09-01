package com.tny.game.net.message.protoex;

import com.tny.game.net.message.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;

@ProtoEx(ProtoExMessageCoder.MESSAGE_HEAD_ID)
public class ProtoExMessageHeader extends AbstractNetMessageHeader {

    @ProtoExField(1)
    private int id;

    @ProtoExField(2)
    private int protocol = -1;

    @ProtoExField(3)
    private int code;

    @ProtoExField(4)
    private int toMessage;

    @ProtoExField(5)
    private long time = -1;

    @ProtoExField(6)
    @Packed(false)
    @ProtoExConf(typeEncode = TypeEncode.EXPLICIT)
    private Object head;

    public ProtoExMessageHeader() {
    }


    @Override
    public int getId() {
        return this.id;
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
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    protected Object getHead() {
        return head;
    }

    @Override
    protected ProtoExMessageHeader setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    protected ProtoExMessageHeader setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    protected ProtoExMessageHeader setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    protected ProtoExMessageHeader setHead(Object head) {
        this.head = head;
        return this;
    }

    @Override
    protected ProtoExMessageHeader setToMessage(int toMessage) {
        this.toMessage = toMessage;
        return this;
    }

    @Override
    protected ProtoExMessageHeader setTime(long time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ProtoExMessageHeader.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("protocol=" + protocol)
                .add("code=" + code)
                .add("toMessage=" + toMessage)
                .add("time=" + time)
                .add("head=" + head)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProtoExMessageHeader)) return false;
        ProtoExMessageHeader that = (ProtoExMessageHeader) o;
        return getId() == that.getId() &&
                getProtocol() == that.getProtocol() &&
                getCode() == that.getCode() &&
                getToMessage() == that.getToMessage() &&
                getTime() == that.getTime() &&
                Objects.equals(getHead(), that.getHead());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProtocol(), getCode(), getToMessage(), getTime(), getHead());
    }

}

