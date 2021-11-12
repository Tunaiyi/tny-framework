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
public class CommonRemoteRelayLink extends BaseRelayLink implements RemoteRelayLink {

	private static final byte[] DEFAULT_ADDRESS = {0, 0, 0, 0};

	private final NetRemoteServeInstance serveInstance;

	public CommonRemoteRelayLink(String key, NetRemoteServeInstance serveInstance, NetRelayTransporter transporter) {
		super(key, serveInstance.getServeName(), serveInstance.getId(), transporter);
		this.serveInstance = serveInstance;
	}

	@Override
	public void auth(String serveName, long serverId) {
		this.write(LinkOpenPacket.FACTORY, new LinkOpenArguments(serveName, serverId, this.getKey()));
	}

	@Override
	public void switchTunnel(RemoteRelayTunnel<?> tunnel) {
		if (tunnel.getLink(this.getServeName()) == this) {
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
