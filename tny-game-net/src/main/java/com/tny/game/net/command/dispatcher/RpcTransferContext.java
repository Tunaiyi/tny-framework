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

import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/13 03:52
 **/
public interface RpcTransferContext extends RpcTransactionContext, RpcEnterCompletable {

    /**
     * 转发
     *
     * @param to            目标
     * @param operationName 操作
     * @return 返回是否成功
     */
    boolean transfer(NetContact to, String operationName);

    /**
     * @return 传送消息
     */
    Message getMessage();

    /**
     * @return 发送服务
     */
    NetContact getFrom();

    /**
     * @return 目标服务
     */
    NetContact getTo();

}
