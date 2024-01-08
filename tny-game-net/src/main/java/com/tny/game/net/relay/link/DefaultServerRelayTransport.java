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
package com.tny.game.net.relay.link;

import com.tny.game.common.context.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 1:02 下午
 */
public class DefaultServerRelayTransport extends AttributeHolder implements ServerRelayTransport {

    private ServerRelayTunnel tunnel;

    private volatile ServerRelayLink link;

    public DefaultServerRelayTransport(ServerRelayLink link) {
        this.link = link;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return tunnel.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return link.getLocalAddress();
    }

    @Override
    public boolean isActive() {
        return link.isActive();
    }

    @Override
    public boolean isClosed() {
        return tunnel.isClosed();
    }

    // @Override
    // public NetAccessMode getAccessMode() {
    //     return tunnel.getAccessMode();
    // }

    @Override
    public boolean close() {
        link.closeTunnel(tunnel);
        return true;
    }

    @Override
    public MessageWriteFuture write(Message message, MessageWriteFuture promise) throws NetException {
        return link.relay(this.tunnel, message, promise);
    }

    @Override
    public MessageWriteFuture write(MessageAllocator maker, MessageFactory factory, MessageContent context) throws NetException {
        return link.relay(this.tunnel, maker, factory, context);
    }

    @Override
    public ServerRelayLink getLink() {
        return link;
    }

    @Override
    public void bind(NetTunnel tunnel) {
        if (this.tunnel == null) {
            this.tunnel = (ServerRelayTunnel) tunnel;
        }
    }

    @Override
    public boolean switchLink(ServerRelayLink link) {
        if (this.link == null) {
            return false;
        }
        if (link == null) {
            return false;
        }
        synchronized (this) {
            if (this.link == null) {
                return false;
            }
            if (this.tunnel.getStatus() == TunnelStatus.OPEN) {
                if (link.getInstanceId() == this.link.getInstanceId()) {
                    this.link = link;
                }
                return true;
            }
            return false;
        }
    }

}
