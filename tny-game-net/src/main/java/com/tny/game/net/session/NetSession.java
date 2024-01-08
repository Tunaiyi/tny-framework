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
package com.tny.game.net.session;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.MessageCommandBox;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public interface NetSession extends Session, MessageReceiver, SentMessageHistory {


    /**
     * 异步发送消息
     *
     * @param tunnel  发送的通道
     * @param content 发送消息上下文
     * @return 返回发送上下文
     */
    MessageSent send(NetTunnel tunnel, MessageContent content);

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
    void online(Certificate certificate) throws AuthFailedException;

    /**
     * 使用指定认证登陆
     *
     * @param tunnel 指定认证
     */
    void online(Certificate certificate, NetTunnel tunnel) throws AuthFailedException;

    /**
     * 通道销毁
     *
     * @param tunnel 销毁通道
     */
    void onUnactivated(NetTunnel tunnel);

    /**
     * @return 当前管道
     */
    NetTunnel tunnel();

    /**
     * @return 消息盒子
     */
    MessageCommandBox getCommandBox();

    /**
     * @return 获取SessionContext上下文
     */
    SessionContext getContext();

    void setSendMessageCachedSize(int messageSize);

    /**
     * 关闭断开连接
     */
    boolean closeWhen(SessionStatus status);


}
