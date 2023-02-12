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

import com.tny.game.net.base.*;
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

    private final NetRemoteServeInstance serveInstance;

    public CommonClientRelayLink(String key, NetRemoteServeInstance serveInstance, RelayTransporter transporter) {
        super(NetAccessMode.CLIENT, key, serveInstance.serviceType(), serveInstance.serviceName(), serveInstance.getId(), transporter);
        this.serveInstance = serveInstance;
    }

    @Override
    public void auth(RpcServiceType serviceType, String service, long serverId) {
        this.write(LinkOpenPacket.FACTORY, new LinkOpenArguments(serviceType, service, serverId, this.getKey()));
    }

    @Override
    public void switchTunnel(ClientRelayTunnel<?> tunnel) {
        if (tunnel.getLink(this.getService()) == this) {
            this.write(TunnelSwitchLinkPacket.FACTORY, new TunnelVoidArguments(tunnel));
        }
    }

    @Override
    public void delinkTunnel(RelayTunnel<?> tunnel) {
    }

    @Override
    public void openTunnel(RelayTunnel<?> tunnel) {
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
    //	public boolean registerTunnel(NetRelayTunnel<?> tunnel) {
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
