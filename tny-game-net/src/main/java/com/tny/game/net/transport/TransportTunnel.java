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

import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;

import java.net.InetSocketAddress;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class TransportTunnel<S extends NetSession, T extends MessageTransport> extends BaseNetTunnel<S> {

    protected volatile T transport;

    protected TransportTunnel(long id, T transport, S session, NetAccessMode accessMode, NetworkContext context) {
        super(id, accessMode, context, session);
        if (transport != null) {
            this.transport = transport;
            this.transport.bind(this);
        }
    }

    protected TransportTunnel(long id, T transport, NetAccessMode accessMode, NetworkContext context) {
        super(id, accessMode, context);
        if (transport != null) {
            this.transport = transport;
            this.transport.bind(this);
        }
    }

    protected MessageTransport getTransport() {
        return this.transport;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.transport.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.transport.getLocalAddress();
    }

    @Override
    public boolean isActive() {
        T transport = this.transport;
        return this.getStatus() == TunnelStatus.OPEN && transport != null && transport.isActive();
    }

    @Override
    public MessageWriteFuture write(Message message, MessageWriteFuture awaiter) throws NetException {
        if (this.checkAvailable(awaiter)) {
            return this.transport.write(message, awaiter);
        }
        return awaiter;
    }

    @Override
    public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) throws NetException {
        MessageWriteFuture promise = content.getWriteFuture();
        if (this.checkAvailable(promise)) {
            return this.transport.write(allocator, this.getMessageFactory(), content);
        }
        return promise;
    }

    @Override
    protected void onDisconnected() {
    }

    @Override
    protected boolean onOpen() {
        T transport = this.transport;
        if (transport == null || !transport.isActive()) {
            LOGGER.warn("open failed. channel {} is not active", transport);
            return false;
        }
        return true;
    }

    @Override
    protected void onOpened() {
    }

    @Override
    protected void onClose() {
    }

    @Override
    protected void onClosed() {
    }

    protected void onWriteUnavailable() {
        this.close();
    }

    @Override
    protected void doDisconnect() {
        T transport = this.transport;
        if (transport != null && transport.isActive()) {
            try {
                transport.close();
            } catch (Throwable e) {
                LOGGER.error("transport close error", e);
            }
        }
    }

    private boolean checkAvailable(MessageWriteFuture awaiter) {
        if (!this.isActive()) {
            this.onWriteUnavailable();
            if (awaiter != null) {
                awaiter.completeExceptionally(new TunnelDisconnectedException("{} is disconnect", this));
            }
            return false;
        }
        return true;
    }

    //	protected AbstractTunnel<UID, E> setNetTransport(T transport) {
    //		this.transport = transport;
    //		return this;
    //	}

    @Override
    public String toString() {
        return "Tunnel(" + this.getAccessMode() + ")[" + this.getGroup() + "(" + this.getIdentify() + ")]" + this.transport;
    }

}
