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

import com.tny.game.common.result.*;
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
public interface RpcProviderContext extends RpcInvocationContext {

    static RpcProviderContext create(NetTunnel<?> tunnel, NetMessage message) {
        return new RpcProviderInvocationContext(tunnel, message);
    }

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

    /**
     * 静默完成
     *
     * @return 是否完成成功
     */
    default boolean completeSilently() {
        return completeSilently(null);
    }

    /**
     * 静默完成
     *
     * @param error 错误原因
     * @return 是否完成成功
     */
    boolean completeSilently(Throwable error);

    /**
     * 静默完成
     *
     * @param body 错误原因
     * @return 是否完成成功
     */
    boolean completeSilently(ResultCode code, Object body);

    /**
     * 完成并响应
     *
     * @param code 结果码
     * @return 是否完成成功
     */
    default boolean complete(ResultCode code) {
        return complete(code, null);
    }

    /**
     * 完成并响应
     *
     * @param content 响应消息
     * @return 是否完成成功
     */
    default boolean complete(MessageContent content) {
        return complete(content, null);
    }

    /**
     * 完成并响应
     *
     * @param code 结果码
     * @return 是否完成成功
     */
    default boolean complete(ResultCode code, Object body) {
        return complete(code, body, null);
    }

    /**
     * 完成并响应
     *
     * @param code 结果码
     * @return 是否完成成功
     */
    boolean complete(ResultCode code, Throwable error);

    /**
     * 完成并响应
     *
     * @param code 结果码
     * @return 是否完成成功
     */
    boolean complete(ResultCode code, Object body, Throwable error);

    /**
     * 完成并响应
     *
     * @param content 响应消息
     * @param error   错误原因
     * @return 是否完成成功
     */
    boolean complete(MessageContent content, Throwable error);

}
