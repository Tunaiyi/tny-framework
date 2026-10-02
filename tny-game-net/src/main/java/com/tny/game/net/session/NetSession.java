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
