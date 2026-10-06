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
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.rpc.*;

import java.net.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 8:33 下午
 */
public class CommonClientRelayLink extends BaseRelayLink implements ClientRelayLink {

    private static final byte[] DEFAULT_ADDRESS = {0, 0, 0, 0};

    private final NetRelayServeInstance serveInstance;

    public CommonClientRelayLink(String key, NetRelayServeInstance serveInstance, RelayTransport transport) {
        super(NetAccessMode.CLIENT, key, serveInstance.serviceType(), serveInstance.getService(), serveInstance.getId(), transport);
        this.serveInstance = serveInstance;
    }

    @Override
    public void auth(RpcServiceType serviceType, String service, long serverId) {
        this.write(LinkOpenPacket.FACTORY, new LinkOpenArguments(serviceType, service, serverId, this.getKey()));
    }

    @Override
    public void switchTunnel(ClientRelayTunnel tunnel) {
        if (tunnel.getLink(this.getService()) == this) {
            this.write(TunnelSwitchLinkPacket.FACTORY, new TunnelVoidArguments(tunnel));
        }
    }

    @Override
    public void unlinkTunnel(RelayTunnel tunnel) {
    }

    @Override
    public void openTunnel(RelayTunnel tunnel) {
        byte[] address = DEFAULT_ADDRESS;
        int port = 0;
        InetSocketAddress socketAddress = tunnel.getRemoteAddress();
        if (socketAddress != null) {
            port = socketAddress.getPort();
            InetAddress inetAddress = socketAddress.getAddress();
            if (inetAddress != null) {
                byte[] ipAddress = inetAddress.getAddress();
                if (ipAddress != null) {
                    address = ipAddress;
                }
            }
        }
        this.write(TunnelConnectPacket.FACTORY, new TunnelConnectArguments(tunnel, address, port));
    }

    @Override
    protected void onDisconnect() {
        this.serveInstance.disconnected(this);
    }

    @Override
    protected void onOpen() {
        this.serveInstance.register(this);
        super.onOpen();
    }

    @Override
    protected void onClosed() {
        this.serveInstance.relieve(this);
    }

    //	@Override
    //	public boolean registerTunnel(NetRelayTunnel tunnel) {
    //		if (!this.isActive()) {
    //			return false;
    //		}
    //		if (this.tunnelMap.putIfAbsent(tunnel.getId(), tunnel) == null) {
    //			tunnel.attributes().setAttributeIfNoKey(NetRelayAttrKeys.RELAY_LINK, this);
    //			InetSocketAddress address = tunnel.getRemoteAddress();
    //			InetAddress inetAddress = address.getAddress();
    //			TunnelConnectArguments arguments = new TunnelConnectArguments(tunnel.getId(), inetAddress.getAddress(), address.getPort());
    //			this.write(TunnelConnectPacket.FACTORY, arguments, false);
    //			return true;
    //		}
    //		return false;
    //	}

}
