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
package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;

import java.net.InetSocketAddress;

/**
 * <p>
 */
public class MockNetTunnel extends AttributeHolder implements NetTunnel {

    private long accessId;

    private final NetAccessMode mode;

    private TunnelStatus state;

    private NetSession session;

    private final InetSocketAddress address = new InetSocketAddress(7100);

    private int pingTimes = 0;

    private int pongTimes = 0;

    private int writeTimes = 0;

    private boolean bindSuccess = true;

    private boolean writeSuccess = true;

    private final NetBootstrapContext context;

    public MockNetTunnel(NetAccessMode mode) {
        this.state = TunnelStatus.OPEN;
        this.mode = mode;
        this.context = new NetBootstrapContext();
    }

    @Override
    public TunnelEventWatches events() {
        return null;
    }

    @Override
    public long getId() {
        return 1;
    }

    @Override
    public long getAccessId() {
        return this.accessId;
    }

    @Override
    public NetAccessMode getAccessMode() {
        return this.mode;
    }

    @Override
    public boolean isActive() {
        return this.state == TunnelStatus.OPEN;
    }

    @Override
    public boolean isOpen() {
        return this.state == TunnelStatus.OPEN;
    }

    @Override
    public TunnelStatus getStatus() {
        return this.state;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.address;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.address;
    }

    @Override
    public NetSession getSession() {
        return this.session;
    }


    @Override
    public void setAccessId(long accessId) {
        this.accessId = accessId;
    }

    @Override
    public void ping() {
        this.pingTimes++;
    }

    @Override
    public void pong() {
        this.pongTimes++;
    }

    @Override
    public boolean open() {
        this.state = TunnelStatus.OPEN;
        return true;
    }

    @Override
    public void disconnect() {
        // 幂等守卫 + 状态前置（对齐 BaseNetTunnel 关序，fix-executor-exception-visibility D4）
        if (this.state == TunnelStatus.SUSPEND || this.state == TunnelStatus.CLOSED) {
            return;
        }
        this.state = TunnelStatus.SUSPEND;
        NetSession session = this.session;
        if (session != null) {
            session.onUnactivated(this);
        }
    }

    @Override
    public void reset() {
        this.state = TunnelStatus.INIT;

    }

    @Override
    public boolean bind(NetSession session) {
        if (this.bindSuccess) {
            this.session = session;
            return true;
        }
        return false;
    }

    @Override
    public NetBootstrapContext getContext() {
        return this.context;
    }

    @Override
    public MessageWriteFuture write(Message message, MessageWriteFuture promise) throws NetException {
        if (promise == null) {
            promise = new MessageWriteFuture();
        }
        if (this.writeSuccess) {
            this.writeTimes++;
            promise.complete(null);
        } else {
            promise.completeExceptionally(new RuntimeException());
        }
        return promise;
    }

    @Override
    public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) throws NetException {
        this.writeTimes++;
        return null;
    }

    @Override
    public long getIdentify() {
        return this.session.getIdentify();
    }

    @Override
    public Certificate getCertificate() {
        return this.session.getCertificate();
    }

    @Override
    public boolean isAuthenticated() {
        return this.session.isAuthenticated();
    }

    @Override
    public boolean isClosed() {
        return this.state == TunnelStatus.CLOSED;
    }

    @Override
    public boolean close() {
        if (this.state == TunnelStatus.CLOSED) {
            return false;
        }
        this.state = TunnelStatus.CLOSED;
        NetSession session = this.session;
        if (session != null) {
            session.onUnactivated(this);
        }
        return true;
    }

    public boolean receive(RpcEnterContext context) {
        return this.session.receive(context);
    }

    @Override
    public boolean receive(NetMessage message) {
        var rpcContext = RpcTransactionContext.createEnter(this, message, true);
        return this.session.receive(rpcContext);
    }

    @Override
    public MessageSent send(MessageContent content) {
        return this.session.send(this, content);
    }

    public MockNetTunnel setBindSuccess(boolean bindSuccess) {
        this.bindSuccess = bindSuccess;
        return this;
    }

    public MockNetTunnel setWriteSuccess(boolean writeSuccess) {
        this.writeSuccess = writeSuccess;
        return this;
    }

    public int getWriteTimes() {
        return this.writeTimes;
    }

    public int getPingTimes() {
        return this.pingTimes;
    }

    public int getPongTimes() {
        return this.pongTimes;
    }

}
