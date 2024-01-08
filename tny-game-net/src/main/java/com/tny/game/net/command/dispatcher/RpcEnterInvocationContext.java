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

import com.tny.game.common.context.*;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 01:40
 **/
class RpcEnterInvocationContext extends CompletableRpcTransactionContext implements RpcEnterContext {

    private final NetTunnel tunnel;

    private final RpcMonitor rpcMonitor;

    private final AtomicBoolean running = new AtomicBoolean();

    private NetContact to;

    private boolean forward;

    RpcEnterInvocationContext(NetTunnel tunnel, NetMessage message, boolean async) {
        this(tunnel, message, async, ContextAttributes.create());
    }

    RpcEnterInvocationContext(NetTunnel tunnel, NetMessage message, boolean async, Attributes attributes) {
        super(message, async, attributes);
        this.tunnel = as(tunnel);
        if (tunnel != null) {
            this.rpcMonitor = tunnel.getContext().getRpcMonitor();
        } else {
            this.rpcMonitor = null;
        }
    }

    @Override
    public Session getSession() {
        return as(this.tunnel.getSession());
    }


    @Override
    public RpcTransactionMode getMode() {
        return RpcTransactionMode.ENTER;
    }

    @Override
    public NetContact getContact() {
        return this.tunnel;
    }

    @Override
    public boolean invoke(String operationName) {
        return prepare(operationName);
    }

    @Override
    public boolean transfer(NetContact to, String operationName) {
        return prepare(operationName, () -> {
            this.to = to;
            this.forward = true;
        });
    }

    @Override
    protected void onPrepare() {
        if (this.forward) {
            rpcMonitor.onTransfer(this);
        } else {
            rpcMonitor.onBeforeInvoke(this);
        }
    }

    @Override
    void onComplete(MessageContent content, Throwable exception) {
        if (this.forward) {
            rpcMonitor.onTransfered(this, content, exception);
        } else {
            rpcMonitor.onAfterInvoke(this, content, cause);
        }
    }

    @Override
    void onReturn(MessageContent content) {
        RpcMessageAide.send(tunnel, content);
    }

    @Override
    public boolean resume() {
        if (isValid() && running.compareAndSet(false, true)) {
            rpcMonitor.onResume(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean suspend() {
        if (isValid() && running.compareAndSet(true, false)) {
            rpcMonitor.onSuspend(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public NetMessage netMessage() {
        return message;
    }

    @Override
    public Message getMessage() {
        return this.message;
    }

    @Override
    public NetContact getFrom() {
        return tunnel;
    }

    @Override
    public NetContact getTo() {
        return to;
    }

    @Override
    public RpcMonitor rpcMonitor() {
        return rpcMonitor;
    }

    @Override
    public NetworkContext networkContext() {
        return tunnel.getContext();
    }

    @Override
    public NetTunnel netTunnel() {
        return as(this.tunnel);
    }

    @Override
    public boolean isValid() {
        return this.tunnel != null && this.message != null;
    }

}
