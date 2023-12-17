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

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/21 02:44
 **/
class RpcExitInvocationContext extends BaseRpcTransactionContext implements RpcExitContext {

    private final MessageContent content;

    private final Endpoint endpoint;

    private final RpcMonitor rpcMonitor;

    RpcExitInvocationContext(Endpoint endpoint, MessageContent content, boolean async, RpcMonitor rpcMonitor) {
        super(async);
        this.content = content;
        this.endpoint = endpoint;
        this.rpcMonitor = rpcMonitor;
    }

    @Override
    public boolean invoke(String operationName) {
        return prepare(operationName);
    }

    @Override
    public RpcTransactionMode getMode() {
        return RpcTransactionMode.EXIT;
    }

    @Override
    public MessageSubject getMessageSubject() {
        return content;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public NetContact getContact() {
        return endpoint;
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
        rpcMonitor.onAfterInvoke(this, content, getCause());
    }

    private void onComplete(Message message) {
        rpcMonitor.onAfterInvoke(this, message, getCause());
    }

}
