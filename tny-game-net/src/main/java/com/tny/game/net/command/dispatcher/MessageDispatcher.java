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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.util.Collection;

/**
 * 请求派发器
 *
 * @author KGTny
 */
@UnitInterface
public interface MessageDispatcher {

    /**
     * 获取处理名
     *
     * @param message 处理名
     * @return 返回
     */
    String getNameOf(MessageSchema message);

    /**
     * 派发消息事件
     * <p>
     * <p>
     * 派发消息事件到相对应的Controller<br>
     *
     * @param context rpc上下文
     * @return 分派的命令
     */
    RpcCommand dispatch(RpcEnterContext context) throws RpcInvokeException;

    /**
     * 添加请求派发错误监听器
     * <p>
     * <p>
     * 增加请求派发错误监听器<br>
     *
     * @param listener 添加的请求派发错误监听器
     */
    void addCommandListener(MessageCommandListener listener);

    /**
     * 添加请求派发错误监听器列表
     * <p>
     * <p>
     * 增加请求派发错误监听器<br>
     *
     * @param listeners 添加的请求派发错误监听器列表
     */
    void addCommandListener(Collection<MessageCommandListener> listeners);

    /**
     * 移除请求派发错误监听器
     * <p>
     * <p>
     * 移除指定的请求派发错误监听器<br>
     *
     * @param listener 移除的请求派发错误监听器
     */
    void removeCommandListener(MessageCommandListener listener);

    /**
     * 清除请求派发错误监听器
     * <p>
     * <p>
     * 清除所有请求派发错误监听器<br>
     */
    void clearCommandListeners();

    /**
     * 判断是否可以分发
     *
     * @param head 请求头
     * @return 可以分发返回ture, 否则返回 false
     */
    boolean isCanDispatch(MessageHead head);

}