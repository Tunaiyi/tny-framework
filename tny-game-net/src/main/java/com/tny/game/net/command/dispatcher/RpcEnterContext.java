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

import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.Executor;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/13 03:52
 **/
public interface RpcEnterContext extends RpcInvocationContext, RpcTransferContext, RpcHandleContext, RpcEnterCompletable {

    //    /**
    //     * @return 准备
    //     */
    //    boolean forward(NetMessager to, String operationName);

    /**
     * 恢复
     */
    boolean resume();

    /**
     * 挂起
     */
    boolean suspend();

    /**
     * @return 运行中
     */
    boolean isRunning();

    /**
     * @return 获取消息
     */
    NetMessage netMessage();

    /**
     * @return 获取通道
     */
    <U> NetTunnel<U> netTunnel();

    /**
     * @return rpc监控
     */
    RpcMonitor rpcMonitor();

    /**
     * @return rpc监控
     */
    NetworkContext networkContext();

    /**
     * @return 当前用户执行器
     */
    Executor executor();

}
