package com.tny.game.net.relay;

import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
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

	private final RemoteRelayExplorer remoteRelayExplorer;

	private final NetworkContext networkContext;

	public RemoteRelayPacketProcessor(RemoteRelayExplorer remoteRelayExplorer, NetworkContext networkContext) {
		super(remoteRelayExplorer);
		this.remoteRelayExplorer = remoteRelayExplorer;
		this.networkContext = networkContext;
	}

	@Override
	public void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet) {
		checkLink(link, packet);
		TunnelRelayArguments arguments = packet.getArguments();
		RemoteRelayTunnel<?> tunnel = remoteRelayExplorer.getTunnel(arguments.getInstanceId(), arguments.getTunnelId());
		if (tunnel == null) {
			RelayPacket.release(packet);
			link.write(TunnelDisconnectPacket.FACTORY, new TunnelVoidArguments(arguments));
			LOGGER.warn("{} 转发消息 {} 到 tunnel[{}], 未找到目标 tunnel", link, packet, arguments.getTunnelId());
			return;
		}
		Message message = arguments.getMessage();
		if (message == null) {
			LOGGER.warn("{} 转发消息 {} 到 tunnel[{}], message 为 null", link, packet, arguments.getTunnelId());
			return;
		}
		tunnel.receive(message);
	}

	@Override
	public void onLinkOpen(NetRelayTransporter transporter, LinkOpenPacket packet) {
		LinkOpenArguments arguments = packet.getArguments();
		LOGGER.info("[ RelayLink({}) [{} ==> {}] ]  接受连接", NetRelayLink.idOf(arguments.getCluster(), arguments.getInstance(), arguments.getKey()),
				transporter.getLocalAddress(), transporter.getRemoteAddress());
		remoteRelayExplorer.acceptOpenLink(transporter, arguments.getCluster(), arguments.getInstance(), arguments.getKey());
	}

	@Override
	public void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet) {
		checkLink(link, packet);
		TunnelConnectArguments arguments = packet.getArguments();
		LOGGER.info("[ RelayLink({}) [{} ==> {}] ] Tunnel连接接受 [ RelayTunnel({}) ]",
				link.getId(), link.getLocalAddress(), link.getRemoteAddress(), arguments.getTunnelId());
		remoteRelayExplorer.acceptConnectTunnel(link, this.networkContext,
				arguments.getInstanceId(), arguments.getTunnelId(), arguments.getIp(), arguments.getPort());
	}

	@Override
	public void onTunnelSwitchLink(NetRelayLink link, TunnelSwitchLinkPacket packet) {
		checkLink(link, packet);
		TunnelVoidArguments arguments = packet.getArguments();
		LOGGER.info("[ RelayLink({}) [{} ==> {}] ] Tunnel切换连接 [ RelayTunnel({}) ]",
				link.getId(), link.getLocalAddress(), link.getRemoteAddress(), arguments.getTunnelId());
		remoteRelayExplorer.switchTunnelLink(link, arguments.getInstanceId(), arguments.getTunnelId());
	}

}
