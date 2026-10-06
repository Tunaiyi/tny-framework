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

import com.tny.game.common.result.*;
import com.tny.game.net.message.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public abstract class MessageContent extends BaseMessageHeaderContainer implements MessageSent, MessageSubject {

    /**
     * @return 获取结果码
     */
    public abstract ResultCode getResultCode();

    /**
     * @param header 头部信息
     * @return 返回 context 自身
     */
    public abstract MessageContent withHeader(MessageHeader<?> header);

    /**
     * @param headers 头部信息列表
     * @return 返回 context 自身
     */
    public abstract MessageContent withHeaders(Collection<MessageHeader<?>> headers);

    /**
     * @param body 设置 Message Body
     * @return 返回 context 自身
     */
    public abstract MessageContent withBody(Object body);

    /**
     * 设置写出等待对象
     *
     * @return 返回 context 自身
     */
    public abstract MessageContent willWriteFuture();

    /**
     * 获取写出等待对象
     *
     * @return 返回 context 自身
     */
    public abstract MessageWriteFuture getWriteFuture();

    /**
     * 取消
     *
     * @param mayInterruptIfRunning 打断所欲运行
     */
    public abstract void cancel(boolean mayInterruptIfRunning);

    /**
     * 取消
     */
    public abstract void cancel(Throwable throwable);

}