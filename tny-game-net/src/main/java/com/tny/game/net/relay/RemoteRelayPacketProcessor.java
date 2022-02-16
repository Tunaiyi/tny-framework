package com.tny.game.net.relay;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * 本地数据包处理器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public class RemoteRelayPacketProcessor extends BaseRelayPacketProcessor {

	private final RemoteRelayExplorer remoteRelayExplorer;

	public RemoteRelayPacketProcessor(RemoteRelayExplorer remoteRelayExplorer) {
		super(remoteRelayExplorer);
		this.remoteRelayExplorer = remoteRelayExplorer;
	}

	@Override
	public void onLinkOpen(RelayTransporter transporter, LinkOpenPacket packet) {
	}

	@Override
	public void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet) {
	}

	@Override
	public void onTunnelSwitchLink(NetRelayLink link, TunnelSwitchLinkPacket packet) {
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
		tunnel.write(message, null);
	}

}
