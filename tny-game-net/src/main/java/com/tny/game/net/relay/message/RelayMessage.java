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
package com.tny.game.net.relay.message;

import com.tny.game.common.context.*;
import com.tny.game.common.type.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 5:48 下午
 */
public class RelayMessage implements NetMessage {

    /**
     * 释放需要转发
     */
    private final boolean relay;

    /**
     * 消息
     */
    private final NetMessage message;

    public RelayMessage(NetMessage message) {
        this.message = message;
        this.relay = message.isRelay();
    }

    @Override
    public boolean isRelay() {
        return relay;
    }

    @Override
    public void relay(boolean value) {
    }

    @Override
    public long getId() {
        return message.getId();
    }

    @Override
    public int getLine() {
        return message.getLine();
    }

    @Override
    public MessageMode getMode() {
        return message.getMode();
    }

    @Override
    public long getToMessage() {
        return message.getToMessage();
    }

    @Override
    public int getCode() {
        return message.getCode();
    }

    @Override
    public int getProtocolId() {
        return message.getProtocolId();
    }

    @Override
    public long getTime() {
        return message.getTime();
    }

    @Override
    public MessageHead getHead() {
        return message.getHead();
    }

    @Override
    public Attributes attributes() {
        return message.attributes();
    }

    @Override
    public boolean existBody() {
        return message.existBody();
    }

    @Override
    public Object getBody() {
        return message.getBody();
    }

    @Override
    public <T> T bodyAs(Class<T> clazz) {
        return message.bodyAs(clazz);
    }

    @Override
    public <T> T bodyAs(ReferenceType<T> clazz) {
        return message.bodyAs(clazz);
    }

    // @Override
    // public MessageType getType() {
    //     return message.getType();
    // }

    @Override
    public void allotMessageId(long id) {
        message.allotMessageId(id);
    }

    @Override
    public <H extends MessageHeader<H>> MessageHeader<H> putHeader(MessageHeader<H> header) {
        return message.putHeader(header);
    }

    @Override
    public <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<H> header) {
        return message.putHeaderIfAbsent(header);
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(String key) {
        return message.removeHeader(key);
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(String key, Class<T> headerClass) {
        return message.removeHeader(key, headerClass);
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(MessageHeaderKey<T> key) {
        return message.removeHeader(key);
    }

    @Override
    public void removeHeaders(Iterable<String> keys) {
        message.removeHeaders(keys);
    }

    @Override
    public void removeAllHeaders() {
        message.removeAllHeaders();
    }

    @Override
    public boolean isOwn(Protocol protocol) {
        return message.isOwn(protocol);
    }

    public void release() {
        OctetMessageBody body = message.bodyAs(OctetMessageBody.class);
        if (body != null) {
            body.release();
        }
    }

}
