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

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 12:02 下午
 */
public class GeneralServerRelayTunnel extends ServerTransportTunnel<NetSession, MessageTransport> implements ServerRelayTunnel {

    private final long instanceId;

    private final InetSocketAddress remoteAddress;

    private final ServerRelayTransport relayTransport;

    public GeneralServerRelayTunnel(long instanceId, long id, ServerRelayTransport relayTransport,
            InetSocketAddress remoteAddress, NetworkContext context) {
        super(id, relayTransport, context);
        this.relayTransport = relayTransport;
        this.instanceId = instanceId;
        this.remoteAddress = remoteAddress;
        this.bind(new CommonSession(Certificates.anonymous(), context, this, 0));
    }

    @Override
    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public MessageWriteFuture relay(RpcTransferContext rpcContext, boolean needPromise) {
        MessageWriteFuture promise = needPromise ? new MessageWriteFuture() : null;
        rpcContext.transfer(relayTransport.getLink(), RpcTransactionContext.relayOperation(rpcContext.getMessage()));
        this.write(rpcContext.getMessage(), promise);
        rpcContext.completeSilently();
        return promise;
    }

    @Override
    public boolean switchLink(ServerRelayLink link) {
        return this.relayTransport.switchLink(link);
    }

    //	@Override
    //	public void onLinkDisconnect(NetRelayLink link) {
    //		this.close();
    //	}
    //
    //	@Override
    //	public void disconnectOnLink(NetRelayLink link) {
    //		this.disconnect();
    //	}

}
