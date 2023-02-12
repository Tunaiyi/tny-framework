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
package com.tny.game.net.endpoint;

import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.MessageCommandBox;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public interface NetEndpoint<UID> extends Endpoint<UID>, MessageReceiver, SentMessageHistory {

    /**
     * 处理收到消息
     *
     * @param rpcContext 接受信息
     */
    @Override
    boolean receive(RpcEnterContext rpcContext);

    /**
     * 异步发送消息
     *
     * @param tunnel  发送的通道
     * @param content 发送消息上下文
     * @return 返回发送上下文
     */
    SendReceipt send(NetTunnel<UID> tunnel, MessageContent content);

    /**
     * 分配生成消息
     *
     * @param messageFactory 消息工厂
     * @param content        发送内容
     * @return 返回创建消息
     */
    NetMessage createMessage(MessageFactory messageFactory, MessageContent content);

    /**
     * 使用指定认证登陆
     *
     * @param tunnel 指定认证
     */
    void online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws AuthFailedException;

    /**
     * 通道销毁
     *
     * @param tunnel 销毁通道
     */
    void onUnactivated(NetTunnel<UID> tunnel);

    /**
     * @return 当前管道
     */
    NetTunnel<UID> tunnel();

    /**
     * @return 消息盒子
     */
    MessageCommandBox getCommandBox();

    /**
     * 载入消息盒子
     *
     * @param commandTaskBox 消息
     */
    void takeOver(MessageCommandBox commandTaskBox);

    /**
     * @return 获取EndpointContext上下文
     */
    EndpointContext getContext();

    /**
     * 关闭断开连接
     */
    boolean closeWhen(EndpointStatus status);

}
