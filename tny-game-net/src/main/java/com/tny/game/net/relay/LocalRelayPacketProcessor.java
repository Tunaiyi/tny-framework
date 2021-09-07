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
public class LocalRelayPacketProcessor extends BaseRelayPacketProcessor {

	private final LocalRelayExplorer localRelayExplorer;

	public LocalRelayPacketProcessor(LocalRelayExplorer localRelayExplorer) {
		super(localRelayExplorer);
		this.localRelayExplorer = localRelayExplorer;
	}

	@Override
	public void onLinkOpen(NetRelayTransporter transporter, LinkOpenPacket packet) {
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
		LocalRelayTunnel<?> tunnel = localRelayExplorer.getTunnel(arguments.getInstanceId(), arguments.getTunnelId());
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
