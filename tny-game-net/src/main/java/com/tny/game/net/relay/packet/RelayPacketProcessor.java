package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 12:31 下午
 */
public interface RelayPacketProcessor {

	void onLinkOpen(NetRelayTransporter transporter, LinkOpenPacket packet);

	void onLinkOpened(NetRelayLink link, LinkOpenedPacket packet);

	void onLinkClose(NetRelayLink link, LinkClosePacket packet);

	void onLinkHeartBeat(NetRelayLink link, LinkHeartBeatPacket packet);

	void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet);

	void onTunnelConnected(NetRelayLink link, TunnelConnectedPacket packet);

	void onTunnelDisconnect(NetRelayLink link, TunnelDisconnectPacket packet);

	void onTunnelSwitchLink(NetRelayLink link, TunnelSwitchLinkPacket packet);

	void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet);

}
