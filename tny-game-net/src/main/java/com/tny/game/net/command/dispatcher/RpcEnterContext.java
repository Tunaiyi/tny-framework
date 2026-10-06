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

import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/13 03:52
 **/
public interface RpcEnterContext extends RpcInvocationContext, RpcTransferContext, RpcHandleContext {

    //    /**
    //     * @return 准备
    //     */
    //    boolean forward(NetContact to, String operationName);

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
     * @return 获取通道
     */
    NetMessage netMessage();

    /**
     * @return 获取通道
     */
    NetTunnel netTunnel();

    /**
     * @return rpc监控
     */
    RpcMonitor rpcMonitor();

    /**
     * @return rpc监控
     */
    NetworkContext networkContext();

}
