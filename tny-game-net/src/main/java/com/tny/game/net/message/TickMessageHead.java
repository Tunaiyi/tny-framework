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
        return MessageAide.EMPTY_MESSAGE_ID;
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
    public <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<?> header) {
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
