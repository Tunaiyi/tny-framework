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
package com.tny.game.net.transport;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface NetTunnel extends Tunnel, MessageSender {

    /**
     * 接受消息
     *
     * @param message 消息
     */
    boolean receive(NetMessage message);

    /**
     * 设置访问 Id
     *
     * @param accessId 访问 Id
     */
    void setAccessId(long accessId);

    /**
     * ping
     */
    void ping();

    /**
     * pong
     */
    void pong();

    /**
     * 打开
     */
    boolean open();

    /**
     * 断开
     */
    void disconnect();

    /**
     * 断开并重置状态
     */
    void reset();

    /**
     * 会话
     *
     * @param session 会话
     * @return 返回是否绑定成功
     */
    boolean bind(NetSession session);

    /**
     * 写出消息
     *
     * @param message 发送消息
     * @param promise 发送promise
     */
    MessageWriteFuture write(Message message, MessageWriteFuture promise) throws NetException;

    /**
     * 写出消息
     *
     * @param content 发送消息
     */
    MessageWriteFuture write(MessageAllocator allocator, MessageContent content) throws NetException;

    /**
     * @return message factory
     */
    default MessageFactory getMessageFactory() {
        return this.getContext().getMessageFactory();
    }

    /**
     * @return 获取上下文
     */
    NetworkContext getContext();

    /**
     * @return 获取绑定中断
     */
    @Override
    NetSession getSession();

}

