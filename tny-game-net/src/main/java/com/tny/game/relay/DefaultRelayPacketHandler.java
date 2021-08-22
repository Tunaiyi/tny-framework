//package com.tny.game.relay;
//
//import com.tny.game.relay.exception.*;
//import com.tny.game.relay.packet.*;
//import com.tny.game.relay.packet.arguments.*;
//import com.tny.game.relay.transport.*;
//import org.slf4j.*;
//
///**
// * <p>
// *
// * @author : kgtny
// * @date : 2021/5/24 2:13 下午
// */
//public class DefaultRelayPacketHandler implements RelayPacketHandler {
//
//	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultRelayPacketHandler.class);
//
//	@Override
//	public void onPipeOpen(NetRelayLink<?> link, LinkOpenPacket packet) {
//	}
//
//	@Override
//	public void onPipeClose(NetRelayLink<?> link, LinkClosePacket packet) {
//	}
//
//	@Override
//	public void onTunnelConnect(NetRelayLink<?> link, TunnelConnectPacket packet) {
//		TunnelConnectArguments arguments = packet.getArguments();
//		try {
//			link.connectTunnel(packet.getTunnelId(), arguments.getIp(), arguments.getPort());
//		} catch (PipeClosedException e) {
//			LOGGER.error("创建转发 {}:{} Tunnel 失败", arguments.getIp(), arguments.getPort());
//		}
//	}
//
//	@Override
//	public void onTunnelRelay(NetRelayLink<?> link, TunnelRelayPacket packet) {
//		RelayTunnel<?> repeater = link.getTunnel(packet.getTunnelId());
//		if (repeater == null) {
//			link.closeTunnel(packet.getTunnelId());
//		} else {
//			repeater.receive(packet.getArguments().getMessage());
//		}
//	}
//
//	@Override
//	public void onTunnelDisconnect(NetRelayLink<?> link, TunnelDisconnectPacket packet) {
//		link.closeTunnel(packet.getTunnelId());
//	}
//
//	@Override
//	public void onPipeHeartBeat(NetRelayLink<?> link, LinkHeartBeatPacket packet) {
//		RelayTunnel<?> repeater = link.getTunnel(packet.getTunnelId());
//		if (repeater == null) {
//			link.closeTunnel(packet.getTunnelId());
//		} else {
//			repeater.pong();
//		}
//	}
//
//	@Override
//	public void onTunnelDisconnected(NetRelayLink<?> link, TunnelDisconnectedPacket packet) {
//		LOGGER.warn("{} 无法处理Disconnected事件 {}", link, packet);
//	}
//
//	@Override
//	public void onTunnelConnected(NetRelayLink<?> link, TunnelConnectedPacket packet) {
//
//	}
//
//}
