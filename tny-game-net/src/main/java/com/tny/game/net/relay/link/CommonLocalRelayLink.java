package com.tny.game.net.relay.link;

import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;

import java.net.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 8:33 下午
 */
public class CommonLocalRelayLink extends BaseRelayLink implements LocalRelayLink {

	public CommonLocalRelayLink(LocalServeInstance serveInstance, String key, NetRelayTransporter transporter) {
		super(serveInstance.getClusterId(), serveInstance.getId(), key, transporter);
	}

	@Override
	public void auth(String clusterId, long serverId) {
		this.write(LinkOpenPacket.FACTORY, new LinkOpenArguments(clusterId, serverId, this.getKey()));
	}

	@Override
	public boolean bindTunnel(NetRelayTunnel<?> tunnel) {
		if (!this.isActive()) {
			return false;
		}
		if (this.tunnelMap.putIfAbsent(tunnel.getId(), tunnel) == null) {
			tunnel.attributes().setAttributeIfNoKey(NetRelayAttrKeys.RELAY_LINK, this);
			InetSocketAddress address = tunnel.getRemoteAddress();
			InetAddress inetAddress = address.getAddress();
			TunnelConnectArguments arguments = new TunnelConnectArguments(tunnel.getId(), inetAddress.getAddress(), address.getPort());
			this.write(TunnelConnectPacket.FACTORY, arguments, false);
			return true;
		}
		return false;
	}

}
