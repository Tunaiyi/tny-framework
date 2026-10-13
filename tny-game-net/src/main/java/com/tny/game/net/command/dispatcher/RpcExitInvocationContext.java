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
 * @date 2022/12/21 02:44
 **/
class RpcExitInvocationContext extends BaseRpcTransactionContext implements RpcExitContext {

    private final MessageContent content;

    private final Session session;

    private final RpcMonitor rpcMonitor;

    RpcExitInvocationContext(Session session, MessageContent content, boolean async, RpcMonitor rpcMonitor) {
        super(async);
        this.content = content;
        this.session = session;
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
        return session;
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
