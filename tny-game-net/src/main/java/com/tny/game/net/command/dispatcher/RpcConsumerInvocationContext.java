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

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/21 02:44
 **/
class RpcConsumerInvocationContext extends BaseRpcInvocationContext implements RpcConsumerContext {

    private final MessageContent content;

    private final RpcMonitor rpcMonitor;

    private final Endpoint<?> endpoint;

    public RpcConsumerInvocationContext(Endpoint<?> endpoint, MessageContent content, RpcMonitor rpcMonitor) {
        this.content = content;
        this.endpoint = endpoint;
        this.rpcMonitor = rpcMonitor;
    }

    @Override
    public MessageSubject getMessageSubject() {
        return content;
    }

    @Override
    public RpcInvocationMode getInvocationMode() {
        return RpcInvocationMode.EXIT;
    }

    @Override
    public <U> Endpoint<U> getEndpoint() {
        return as(endpoint);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean complete() {
        if (tryCompleted()) {
            onComplete();
            return true;
        }
        return false;
    }

    @Override
    public boolean complete(Message message) {
        if (tryCompleted()) {
            onComplete(message);
            return true;
        }
        return false;
    }

    @Override
    protected void onPrepare() {
        rpcMonitor.onBeforeInvoke(this);
    }

    @Override
    protected void onComplete() {
        rpcMonitor.onInvokeResult(this, content, getCause());
    }

    private void onComplete(Message message) {
        rpcMonitor.onInvokeResult(this, message, getCause());
    }

}
