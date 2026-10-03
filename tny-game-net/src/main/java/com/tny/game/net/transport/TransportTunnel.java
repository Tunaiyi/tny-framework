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

import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
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
        // 丢弃留痕由 checkAvailable 的异常回执承担；载荷在此终结释放，不交给 GC 时点（relay-link 契约）
        releaseMessageBody(message);
        return awaiter;
    }

    private static void releaseMessageBody(Message message) {
        if (message == null) {
            return;
        }
        Object body = message.getBody();
        if (body instanceof OctetMessageBody octet) {
            OctetMessageBody.release(octet);
        }
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
