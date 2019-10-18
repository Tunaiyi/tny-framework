package com.tny.game.net.message.common;

import com.tny.game.net.message.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class CommonMessageHead extends AbstractNetMessageHead {

    private long id;

    private int protocol = -1;

    private long time;

    private long toMessage;

    private int code;

    // protected CommonMessageHead() {
    // }

    public CommonMessageHead(long id, MessageMode mode, int protocol, int code, long toMessage, long time) {
        super(mode);
        this.id = id;
        this.protocol = protocol;
        this.time = time;
        this.toMessage = toMessage;
        this.code = code;
    }

    public CommonMessageHead(long id, MessageSubject subject) {
        super(subject.getMode());
        this.id = id;
        this.protocol = subject.getProtocolNumber();
        this.code = subject.getCode();
        this.toMessage = subject.getToMessage();
        this.time = System.currentTimeMillis();
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

    CommonMessageHead setId(long id) {
        this.id = id;
        return this;
    }

    CommonMessageHead setCode(int code) {
        this.code = code;
        return this;
    }

    CommonMessageHead setTime(long time) {
        this.time = time;
        return this;
    }

    CommonMessageHead setToMessage(long toMessage) {
        this.toMessage = toMessage;
        return this;
    }

    CommonMessageHead setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonMessageHead)) return false;
        CommonMessageHead that = (CommonMessageHead) o;
        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(protocol, that.protocol)
                .append(getTime(), that.getTime())
                .append(getToMessage(), that.getToMessage())
                .append(getCode(), that.getCode())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(protocol)
                .append(getTime())
                .append(getToMessage())
                .append(getCode())
                .toHashCode();
    }
}
