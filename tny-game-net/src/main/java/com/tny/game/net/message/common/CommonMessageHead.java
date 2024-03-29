/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.message.common;

import com.tny.game.net.message.*;
import org.apache.commons.lang3.builder.*;

import java.util.Map;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class CommonMessageHead extends BaseMessageHeaderContainer implements NetMessageHead {

    private long id;

    private int line;

    private int protocol = -1;

    private long time;

    private long toMessage;

    private int code;

    protected volatile MessageMode mode;

    public CommonMessageHead(long id, MessageMode mode, int line, int protocol, int code, long toMessage, long time,
            Map<String, MessageHeader<?>> headers) {
        super(headers);
        this.id = id;
        this.line = line;
        this.mode = mode;
        this.protocol = protocol;
        this.time = time;
        this.toMessage = toMessage;
        this.code = code;
    }

    public CommonMessageHead(long id, MessageSubject subject) {
        super(subject.getAllHeaderMap());
        this.id = id;
        this.line = subject.getLine();
        this.mode = subject.getMode();
        this.protocol = subject.getProtocolId();
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
    public int getProtocolId() {
        return this.protocol;
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public MessageMode getMode() {
        return this.mode;
    }

    @Override
    public void allotMessageId(long id) {
        if (this.id <= -1) {
            this.id = id;
        }
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommonMessageHead)) {
            return false;
        }
        CommonMessageHead that = (CommonMessageHead) o;
        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(this.protocol, that.protocol)
                .append(this.line, that.line)
                .append(getTime(), that.getTime())
                .append(getToMessage(), that.getToMessage())
                .append(getCode(), that.getCode())
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("mode", mode)
                .append("protocol", protocol)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(this.protocol)
                .append(this.line)
                .append(getTime())
                .append(getToMessage())
                .append(getCode())
                .toHashCode();
    }

}
