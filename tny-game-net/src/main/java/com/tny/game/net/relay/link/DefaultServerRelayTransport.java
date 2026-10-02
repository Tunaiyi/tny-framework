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
package com.tny.game.net.relay.link;

import com.tny.game.common.context.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 1:02 下午
 */
public class DefaultServerRelayTransport extends AttributeHolder implements ServerRelayTransport {

    private ServerRelayTunnel tunnel;

    private volatile ServerRelayLink link;

    private final Lock lock = new ReentrantLock();


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
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

}
