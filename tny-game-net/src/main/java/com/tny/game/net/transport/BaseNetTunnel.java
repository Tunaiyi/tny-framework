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

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;

import java.net.InetSocketAddress;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class BaseNetTunnel<UID, E extends NetEndpoint<UID>, T extends MessageTransporter> extends AbstractNetTunnel<UID, E> {

    protected volatile T transporter;

    protected BaseNetTunnel(long id, T transporter, NetAccessMode accessMode, NetworkContext context) {
        super(id, accessMode, context);
        if (transporter != null) {
            this.transporter = transporter;
            this.transporter.bind(this);
        }
    }

    protected MessageTransporter getTransporter() {
        return this.transporter;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.transporter.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.transporter.getLocalAddress();
    }

    @Override
    public boolean isActive() {
        T transporter = this.transporter;
        return this.getStatus() == TunnelStatus.OPEN && transporter != null && transporter.isActive();
    }

    @Override
    public MessageWriteFuture write(Message message, MessageWriteFuture awaiter) throws NetException {
        if (this.checkAvailable(awaiter)) {
            return this.transporter.write(message, awaiter);
        }
        return awaiter;
    }

    @Override
    public MessageWriteFuture write(MessageAllocator allocator, MessageContent context) throws NetException {
        MessageWriteFuture promise = context.getWriteFuture();
        if (this.checkAvailable(promise)) {
            return this.transporter.write(allocator, this.getMessageFactory(), context);
        }
        return promise;
    }

    @Override
    protected void onDisconnected() {
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
    }

    @Override
    protected void doDisconnect() {
        T transporter = this.transporter;
        if (transporter != null && transporter.isActive()) {
            try {
                transporter.close();
            } catch (Throwable e) {
                LOGGER.error("transporter close error", e);
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
    //		this.transporter = transport;
    //		return this;
    //	}

    @Override
    public String toString() {
        return "Tunnel(" + this.getAccessMode() + ")[" + this.getUserGroup() + "(" + this.getUserId() + ")]" + this.transporter;
    }

}
