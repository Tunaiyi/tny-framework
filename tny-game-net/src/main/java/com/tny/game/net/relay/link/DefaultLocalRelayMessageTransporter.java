/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.link;

import com.tny.game.common.context.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 1:02 下午
 */
public class DefaultLocalRelayMessageTransporter extends AttributesHolder implements LocalRelayMessageTransporter {

    private LocalRelayTunnel<?> tunnel;

    private volatile LocalRelayLink link;

    public DefaultLocalRelayMessageTransporter(LocalRelayLink link) {
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

    @Override
    public boolean close() {
        link.closeTunnel(tunnel);
        return true;
    }

    @Override
    public MessageWriteAwaiter write(Message message, MessageWriteAwaiter promise) throws NetException {
        return link.relay(this.tunnel, message, promise);
    }

    @Override
    public MessageWriteAwaiter write(MessageAllocator maker, MessageFactory factory, MessageContext context) throws NetException {
        return link.relay(this.tunnel, maker, factory, context);
    }

    @Override
    public void bind(NetTunnel<?> tunnel) {
        if (this.tunnel == null) {
            this.tunnel = (LocalRelayTunnel<?>)tunnel;
        }
    }

    @Override
    public boolean switchLink(LocalRelayLink link) {
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
