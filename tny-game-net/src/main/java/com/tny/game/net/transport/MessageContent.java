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

import com.tny.game.common.result.*;
import com.tny.game.net.message.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public abstract class MessageContent implements SendReceipt, MessageSubject {

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
    public abstract MessageContent willWriteAwaiter();

    /**
     * 获取写出等待对象
     *
     * @return 返回 context 自身
     */
    public abstract MessageWriteAwaiter getWriteAwaiter();

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