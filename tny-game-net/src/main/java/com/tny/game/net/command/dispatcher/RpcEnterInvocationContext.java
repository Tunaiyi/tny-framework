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
