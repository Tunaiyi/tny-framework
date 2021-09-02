package com.tny.game.net.relay;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.exception.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.rmi.server.UID;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public abstract class BaseRelayPacketProcessor implements RelayPacketProcessor {

	public static final Logger LOGGER = LoggerFactory.getLogger(RelayPacketProcessor.class);

	@Override
	public void onLinkOpened(NetRelayLink link, LinkOpenedPacket packet) {
		checkLink(link, packet);
		LinkOpenedArguments arguments = packet.getArguments();
		if (arguments.isSuccess()) {
			LOGGER.info("[ RelayLink({}) [{} ==> {}] ] 连接成功", link.getId(), link.getLocalAddress(), link.getRemoteAddress());
			link.open();
		} else {
			LOGGER.info("[ RelayLink({}) [{} ==> {}] ] 连接失败", link.getId(), link.getLocalAddress(), link.getRemoteAddress());
			link.close();
		}
	}

	@Override
	public void onLinkClose(NetRelayLink link, LinkClosePacket packet) {
		checkLink(link, packet);
		LOGGER.info("[ RelayLink({}) [{} ==> {}] ] 关闭连接", link.getId(), link.getLocalAddress(), link.getRemoteAddress());
		link.close();
	}

	@Override
	public void onTunnelConnected(NetRelayLink link, TunnelConnectedPacket packet) {
		TunnelConnectedArguments arguments = packet.getArguments();
		if (arguments.isSuccess()) {
			LOGGER.info("[ RelayLink({}) [{} ==> {}] ] Tunnel连接成功 [ RelayTunnel({}) ]",
					link.getId(), link.getLocalAddress(), link.getRemoteAddress(), arguments.getTunnelId());
		} else {
			LOGGER.info("[ RelayLink({}) [{} ==> {}] ] Tunnel连接失败 [ RelayTunnel({}) ]",
					link.getId(), link.getLocalAddress(), link.getRemoteAddress(), arguments.getTunnelId());
		}
	}

	@Override
	public void onTunnelDisconnect(NetRelayLink link, TunnelDisconnectPacket packet) {
		checkLink(link, packet);
		TunnelVoidArguments arguments = packet.getArguments();
		LOGGER.info("[ RelayLink({}) [{} ==> {}] ] Tunnel连接断开 [ RelayTunnel({}) ]",
				link.getId(), link.getLocalAddress(), link.getRemoteAddress(), arguments.getTunnelId());
		link.closeTunnel(arguments.getTunnelId());
	}

	@Override
	public void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet) {
		checkLink(link, packet);
		TunnelRelayArguments arguments = packet.getArguments();
		NetTunnel<UID> tunnel = link.getTunnel(arguments.getTunnelId());
		if (tunnel == null) {
			RelayPacket.release(packet);
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
	public void onLinkHeartBeat(NetRelayLink link, LinkHeartBeatPacket packet) {
		checkLink(link, packet);
		if (packet.getType() == RelayPacketType.LINK_PING) {
			link.pong();
		}
	}

	//	@Override
	//	public void onTunnelDisconnected(NetRelayLink link, TunnelDisconnectedPacket packet) {
	//	}
	//
	//	@Override
	//	public void onTunnelConnected(NetRelayLink link, TunnelConnectedPacket packet) {
	//	}

	protected void checkLink(NetRelayLink link, RelayPacket<?> packet) {
		if (link == null) {
			RelayPacket.release(packet);
			throw new RelayLinkNoExistException("socket未创建转发连接NetRelayLink");
		}
	}

}
