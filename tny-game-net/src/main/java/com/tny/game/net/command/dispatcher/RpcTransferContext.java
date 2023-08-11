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
