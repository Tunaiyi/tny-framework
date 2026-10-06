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

import com.tny.game.common.result.*;

import java.util.*;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class TickMessageHead implements NetMessageHead {

    private final int protocol;

    private final long time;

    protected final MessageMode mode;

    public static TickMessageHead ping() {
        return new TickMessageHead(PING_PONG_PROTOCOL_NUM, MessageMode.PING);
    }

    public static TickMessageHead pong() {
        return new TickMessageHead(PING_PONG_PROTOCOL_NUM, MessageMode.PONG);
    }

    private TickMessageHead(int protocol, MessageMode mode) {
        this.mode = mode;
        this.time = System.currentTimeMillis();
        this.protocol = protocol;
    }

    @Override
    public void allotMessageId(long id) {
    }

    @Override
    public int getProtocolId() {
        return this.protocol;
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public int getCode() {
        return ResultCode.SUCCESS_CODE;
    }

    @Override
    public long getToMessage() {
        return MessageConstants.EMPTY_MESSAGE_ID;
    }

    @Override
    public MessageMode getMode() {
        return mode;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public <T extends MessageHeader<?>> T getHeader(String key, Class<T> headerClass) {
        return null;
    }

    @Override
    public <T extends MessageHeader<?>> List<T> getHeaders(Class<T> headerClass) {
        return Collections.emptyList();
    }

    @Override
    public <T extends MessageHeader<?>> T getHeader(MessageHeaderKey<T> key) {
        return null;
    }

    @Override
    public <H extends MessageHeader<H>> MessageHeader<H> putHeader(MessageHeader<H> header) {
        return null;
    }

    @Override
    public <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<H> header) {
        return null;
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(String key) {
        return null;
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(String key, Class<T> headerClass) {
        return null;
    }

    @Override
    public <T extends MessageHeader<?>> T removeHeader(MessageHeaderKey<T> key) {
        return null;
    }

    @Override
    public void removeHeaders(Iterable<String> keys) {
    }

    @Override
    public void removeAllHeaders() {
    }

    @Override
    public boolean isHasHeaders() {
        return false;
    }

    @Override
    public Map<String, MessageHeader<?>> getAllHeaderMap() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public RpcForwardHeader getForwardHeader() {
        return null;
    }

    @Override
    public List<MessageHeader<?>> getAllHeaders() {
        return Collections.emptyList();
    }

    @Override
    public boolean existHeader(String key) {
        return false;
    }

    @Override
    public boolean existHeader(String key, Class<? extends MessageHeader<?>> headerClass) {
        return false;
    }

    @Override
    public boolean existHeader(MessageHeaderKey<?> key) {
        return false;
    }

}
