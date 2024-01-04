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

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 12:02 下午
 */
public class GeneralServerRelayTunnel extends ServerTunnel<NetSession, MessageTransporter> implements ServerRelayTunnel {

    private final long instanceId;

    private final InetSocketAddress remoteAddress;

    private final ServerRelayTransporter transporter;

    public GeneralServerRelayTunnel(long instanceId, long id, ServerRelayTransporter transporter,
            InetSocketAddress remoteAddress, NetworkContext context) {
        super(id, transporter, context);
        this.transporter = transporter;
        this.instanceId = instanceId;
        this.remoteAddress = remoteAddress;
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
        rpcContext.transfer(transporter.getLink(), RpcTransactionContext.relayOperation(rpcContext.getMessage()));
        this.write(rpcContext.getMessage(), promise);
        rpcContext.completeSilently();
        return promise;
    }

    @Override
    public boolean switchLink(ServerRelayLink link) {
        return this.transporter.switchLink(link);
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
