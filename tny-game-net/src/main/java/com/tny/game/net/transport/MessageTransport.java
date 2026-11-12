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

import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-11 17:45
 */
public interface MessageTransport extends Connection {

    /**
     * 通道通道
     *
     * @param tunnel 通道通道
     */
    void bind(NetTunnel tunnel);

    /**
     * 发送消息
     *
     * @param message 消息
     * @param awaiter 写出等待对象
     * @return 返回promise
     * @throws NetException 写出异常
     */
    MessageWriteFuture write(Message message, MessageWriteFuture awaiter) throws NetException;

    /**
     * 发送消息
     *
     * @param maker   消息创建器
     * @param factory 消息消息工厂
     * @param context 消息上下文
     * @return 返回promise
     * @throws NetException 写出异常
     */
    MessageWriteFuture write(MessageAllocator maker, MessageFactory factory, MessageContent context) throws NetException;

}
