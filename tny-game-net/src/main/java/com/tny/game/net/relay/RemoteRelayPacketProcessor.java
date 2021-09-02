package com.tny.game.net.relay;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public class RemoteRelayPacketProcessor extends BaseRelayPacketProcessor {

	private final RemoteRelayExplorer remoteRelayLinkExplorer;

	public RemoteRelayPacketProcessor(RemoteRelayExplorer remoteRelayLinkExplorer) {
		this.remoteRelayLinkExplorer = remoteRelayLinkExplorer;
	}

	@Override
	public void onLinkOpen(NetRelayTransporter transporter, LinkOpenPacket packet) {
		LinkOpenArguments arguments = packet.getArguments();
		LOGGER.info("[ RelayLink({}) [{} ==> {}] ]  接受连接", NetRelayLink.idOf(arguments.getCluster(), arguments.getInstance(), arguments.getKey()),
				transporter.getLocalAddress(), transporter.getRemoteAddress());
		remoteRelayLinkExplorer.acceptOpenLink(transporter, arguments.getCluster(), arguments.getInstance(), arguments.getKey());
	}

	@Override
	public void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet) {
		checkLink(link, packet);
		TunnelConnectArguments arguments = packet.getArguments();
		LOGGER.info("[ RelayLink({}) [{} ==> {}] ] Tunnel连接接受 [ RelayTunnel({}) ]",
				link.getId(), link.getLocalAddress(), link.getRemoteAddress(), arguments.getTunnelId());
		remoteRelayLinkExplorer.acceptConnectTunnel(link, arguments.getTunnelId(), arguments.getIp(), arguments.getPort());
	}

}
