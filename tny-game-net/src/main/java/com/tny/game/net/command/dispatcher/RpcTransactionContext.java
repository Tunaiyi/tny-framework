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
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/20 14:32
 **/
public interface RpcTransactionContext extends RpcContext {

    static String forwardOperation(MessageSubject message) {
        return "forward[" + message.getProtocolId() + "]" + message.getMode().getMark();
    }

    static String relayOperation(MessageSubject message) {
        return "relay[" + message.getProtocolId() + "]" + message.getMode().getMark();
    }

    static String rpcOperation(Class operation, String method, MessageSubject message) {
        return "[" + message.getProtocolId() + "@" + message.getMode().getMark() + "]" + operation.getSimpleName() + "." + method;
    }

    static String rpcOperation(String method, MessageSubject message) {
        return "[" + message.getProtocolId() + "@" + message.getMode().getMark() + "]" + method;
    }

    static String returnOperation(MessageSubject message) {
        return "return" + "[" + message.getProtocolId() + "@" + message.getMode().getMark() + "]";
    }

    static String errorOperation(MessageSubject message) {
        return "error" + "[" + message.getProtocolId() + "@" + message.getMode().getMark() + "]";
    }

    static RpcExitContext createExit(Session session, MessageContent content, boolean async, RpcMonitor rpcMonitor) {
        return new RpcExitInvocationContext(session, content, async, rpcMonitor);
    }

    static RpcEnterContext createEnter(NetTunnel tunnel, NetMessage message, boolean async) {
        return new RpcEnterInvocationContext(tunnel, message, async);
    }

    static RpcTransferContext createRelay(NetContact from, NetMessage message, RpcMonitor rpcMonitor, boolean async) {
        return new RpcRelayInvocationContext(from, message, rpcMonitor, async);
    }

    /**
     * @return 请求模式
     */
    RpcTransactionMode getMode();

    /**
     * @return 发送者
     */
    NetContact getContact();

    boolean isCompleted();

    /**
     * @return 操作名
     */
    String getOperationName();

    /**
     * 失败并响应
     *
     * @param error 错误原因
     * @return 是否完成成功
     */
    boolean complete(Throwable error);

    /**
     * 成功并响应
     *
     * @return 是否完成成功
     */
    boolean complete();

    /**
     * @return 获取错误原因
     */
    Throwable getCause();

    /**
     * @return 是否错误(异常)
     */
    boolean isError();

    /**
     * @return 是否异步
     */
    boolean isAsync();

}
