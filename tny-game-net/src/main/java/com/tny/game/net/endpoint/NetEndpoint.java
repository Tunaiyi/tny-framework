/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.endpoint;

import com.tny.game.net.command.*;
import com.tny.game.net.command.task.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public interface NetEndpoint<UID> extends Endpoint<UID>, SentMessageHistory, Receiver {

    /**
     * 处理收到消息
     *
     * @param tunnel  通道
     * @param message 消息
     */
    boolean receive(NetTunnel<UID> tunnel, Message message);

    /**
     * 异步发送消息
     *
     * @param tunnel         发送的通道
     * @param messageContext 发送消息上下文
     * @return 返回发送上下文
     */
    SendReceipt send(NetTunnel<UID> tunnel, MessageContext messageContext);

    /**
     * 分配生成消息
     *
     * @param messageFactory 消息工厂
     * @param context        发送内容
     * @return 返回创建消息
     */
    NetMessage createMessage(MessageFactory messageFactory, MessageContext context);

    /**
     * 使用指定认证登陆
     *
     * @param tunnel 指定认证
     */
    void online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws ValidatorFailException;

    /**
     * 通道销毁
     *
     * @param tunnel 销毁通道
     */
    void onUnactivated(NetTunnel<UID> tunnel);

    /**
     * @return 消息盒子
     */
    CommandTaskBox getCommandTaskBox();

    /**
     * 载入消息盒子
     *
     * @param commandTaskBox 消息
     */
    void takeOver(CommandTaskBox commandTaskBox);

    /**
     * @return 获取EndpointContext上下文
     */
    EndpointContext getContext();

    /**
     * 关闭断开连接
     */
    boolean closeWhen(EndpointStatus status);

}
