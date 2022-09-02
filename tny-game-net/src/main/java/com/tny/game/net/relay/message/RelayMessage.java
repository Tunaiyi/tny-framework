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

    @Override
    public MessageType getType() {
        return message.getType();
    }

    @Override
    public void allotMessageId(long id) {
        message.allotMessageId(id);
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
