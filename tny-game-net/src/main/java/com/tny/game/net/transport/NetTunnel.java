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

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface NetTunnel extends Tunnel, Transport, MessageSender {

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
     * 终端 Endpoint
     *
     * @param endpoint 终端
     * @return 返回是否绑定成功
     */
    boolean bind(NetEndpoint endpoint);

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
    NetEndpoint getEndpoint();

}

