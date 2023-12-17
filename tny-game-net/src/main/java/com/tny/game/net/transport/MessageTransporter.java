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
package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-11 17:45
 */
public interface MessageTransporter extends Connection {

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
