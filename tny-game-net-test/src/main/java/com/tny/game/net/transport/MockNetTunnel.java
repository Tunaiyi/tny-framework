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
package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;

import java.net.InetSocketAddress;

/**
 * <p>
 */
public class MockNetTunnel extends AttributeHolder implements NetTunnel<Long> {

    private long accessId;

    private final NetAccessMode mode;

    private TunnelStatus state;

    private NetEndpoint<Long> endpoint;

    private final InetSocketAddress address = new InetSocketAddress(7100);

    private int pingTimes = 0;

    private int pongTimes = 0;

    private int writeTimes = 0;

    private boolean bindSuccess = true;

    private boolean writeSuccess = true;

    private final NetBootstrapContext context;

    public MockNetTunnel(NetEndpoint<Long> endpoint, NetAccessMode mode) {
        this.endpoint = endpoint;
        this.state = TunnelStatus.OPEN;
        this.mode = mode;
        this.context = new NetBootstrapContext();
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
    public NetEndpoint<Long> getEndpoint() {
        return this.endpoint;
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
        this.state = TunnelStatus.SUSPEND;
        this.endpoint.onUnactivated(this);
    }

    @Override
    public void reset() {
        this.state = TunnelStatus.INIT;

    }

    @Override
    public boolean bind(NetEndpoint<Long> endpoint) {
        if (this.bindSuccess) {
            this.endpoint = endpoint;
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
    public MessageWriteFuture write(MessageAllocator allocator, MessageContent context) throws NetException {
        this.writeTimes++;
        return null;
    }

    @Override
    public Long getUserId() {
        return this.endpoint.getUserId();
    }

    @Override
    public Certificate<Long> getCertificate() {
        return this.endpoint.getCertificate();
    }

    @Override
    public boolean isAuthenticated() {
        return this.endpoint.isAuthenticated();
    }

    @Override
    public boolean isClosed() {
        return this.state == TunnelStatus.CLOSED;
    }

    @Override
    public boolean close() {
        this.disconnect();
        this.state = TunnelStatus.CLOSED;
        return true;
    }

    public boolean receive(RpcEnterContext context) {
        return this.endpoint.receive(context);
    }

    @Override
    public boolean receive(NetMessage message) {
        var rpcContext = RpcTransactionContext.createEnter(this, message, true);
        return this.endpoint.receive(rpcContext);
    }

    @Override
    public SendReceipt send(MessageContent content) {
        return this.endpoint.send(this, content);
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
