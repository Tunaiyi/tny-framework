/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.message;

import com.tny.game.common.context.*;
import com.tny.game.common.type.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.builder.*;
import org.apache.commons.lang3.time.DateFormatUtils;

public abstract class AbstractNetMessage extends AttributeHolder implements NetMessage {

    private NetMessageHead head;

    private Object body;

    private boolean relay;

    protected AbstractNetMessage() {
    }

    protected AbstractNetMessage(NetMessageHead head) {
        this.head = head;
    }

    protected AbstractNetMessage(NetMessageHead head, Object body) {
        this.head = head;
        this.body = body;
    }

    @Override
    public void allotMessageId(long id) {
        this.head.allotMessageId(id);
    }

    @Override
    public MessageHead getHead() {
        return this.head;
    }

    @Override
    public Object getBody() {
        return this.body;
    }

    @Override
    public boolean isRelay() {
        return relay;
    }

    @Override
    public void relay(boolean value) {
        relay = value;
    }

    @Override
    public <T> T bodyAs(ReferenceType<T> type) {
        return ObjectAide.convertTo(this.body, type);
    }

    @Override
    public <T> T bodyAs(Class<T> clazz) {
        return ObjectAide.convertTo(this.body, clazz);
    }

    @Override
    public <H extends MessageHeader<H>> MessageHeader<H> putHeader(MessageHeader<H> header) {
        return head.putHeader(header);
    }

    @Override
    public <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<H> header) {
        return head.putHeaderIfAbsent(header);
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(String key) {
        return head.removeHeader(key);
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(String key, Class<T> headerClass) {
        return head.removeHeader(key, headerClass);
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(MessageHeaderKey<T> key) {
        return head.removeHeader(key);
    }

    @Override
    public void removeHeaders(Iterable<String> keys) {
        head.removeHeaders(keys);
    }

    @Override
    public void removeAllHeaders() {
        head.removeAllHeaders();
    }

    @Override
    public boolean existBody() {
        return this.body != null;
    }

    protected AbstractNetMessage setBody(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("id", this.head.getId())
                .append("mode", this.head.getMode())
                .append("protocol", this.head.getProtocolId())
                .append("to", this.head.getToMessage())
                .append("date", DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(this.head.getTime()))
                .append("code", this.head.getCode())
                .append("body", this.body)
                .toString();
    }

}
