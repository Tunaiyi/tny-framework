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
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class ClientTransportTunnel<T extends MessageTransport> extends TransportTunnel<NetSession, T> {

    private final TunnelUnavailableWatch watch;

    public ClientTransportTunnel(long id, T transport, NetSession session, NetworkContext context, TunnelUnavailableWatch watch) {
        super(id, transport, session, NetAccessMode.CLIENT, context);
        this.watch = watch;
    }

    public ClientTransportTunnel(long id, T transport, NetworkContext context, TunnelUnavailableWatch watch) {
        super(id, transport, NetAccessMode.CLIENT, context);
        this.watch = watch;
    }

    public ClientTransportTunnel(long id, NetworkContext context, TunnelUnavailableWatch watch) {
        super(id, null, NetAccessMode.CLIENT, context);
        this.watch = watch;
    }

    @Override
    protected boolean resetSession(NetSession session) {
        return this.session == session;
    }

    @Override
    protected void onDisconnected() {
        if (watch != null) {
            watch.onUnavailable(this);
        }
        this.close();
    }

}
