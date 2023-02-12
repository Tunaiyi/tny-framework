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

    private final NetMessager from;

    private NetMessager to;

    private final RpcMonitor rpcMonitor;

    RpcRelayInvocationContext(NetMessager from, NetMessage message, RpcMonitor rpcMonitor, boolean async) {
        super(message, async);
        this.rpcMonitor = rpcMonitor;
        this.from = from;
        if (from instanceof MessageSender) {
            this.sender = (MessageSender)from;
        } else {
            this.sender = null;
        }
    }

    @Override
    public RpcInvocationMode getInvocationMode() {
        return RpcInvocationMode.TRANSFER;
    }

    @Override
    public NetMessager getMessager() {
        return from;
    }

    @Override
    public boolean transfer(NetMessager to, String operationName) {
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
    public NetMessage netMessage() {
        return message;
    }

    @Override
    public NetMessager getFrom() {
        return from;
    }

    @Override
    public NetMessager getTo() {
        return to;
    }

}
