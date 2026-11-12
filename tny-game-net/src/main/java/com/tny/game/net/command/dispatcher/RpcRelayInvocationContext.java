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
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 01:40
 **/
class RpcRelayInvocationContext extends CompletableRpcTransactionContext implements RpcTransferContext {

    private final MessageSender sender;

    private final NetContact from;

    private NetContact to;

    private final RpcMonitor rpcMonitor;

    RpcRelayInvocationContext(NetContact from, NetMessage message, RpcMonitor rpcMonitor, boolean async) {
        super(message, async);
        this.rpcMonitor = rpcMonitor;
        this.from = from;
        if (from instanceof MessageSender) {
            this.sender = (MessageSender) from;
        } else {
            this.sender = null;
        }
    }

    @Override
    public RpcTransactionMode getMode() {
        return RpcTransactionMode.TRANSFER;
    }

    @Override
    public NetContact getContact() {
        return from;
    }

    @Override
    public boolean transfer(NetContact to, String operationName) {
        return prepare(operationName, () -> this.to = to);
    }

    @Override
    protected void onPrepare() {
        rpcMonitor.onTransfer(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    void onComplete(MessageContent result, Throwable exception) {
        rpcMonitor.onTransfered(this, result, exception);
    }

    @Override
    void onReturn(MessageContent content) {
        if (sender != null) {
            sender.send(content);
        }
    }

    @Override
    public NetMessage getMessage() {
        return message;
    }

    @Override
    public NetContact getFrom() {
        return from;
    }

    @Override
    public NetContact getTo() {
        return to;
    }

}
