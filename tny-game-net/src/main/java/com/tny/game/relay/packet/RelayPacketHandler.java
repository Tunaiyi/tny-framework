package com.tny.game.relay.packet;

import com.tny.game.relay.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 12:31 下午
 */
public interface RelayPacketHandler {

	void onPipeOpen(NetRelayLink link, LinkOpenPacket packet);

	void onPipeClose(NetRelayLink link, LinkClosePacket packet);

	void onPipeHeartBeat(NetRelayLink link, LinkHeartBeatPacket packet);

	void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet);

	void onTunnelConnected(NetRelayLink link, TunnelConnectedPacket packet);

	void onTunnelDisconnect(NetRelayLink link, TunnelDisconnectPacket packet);

	void onTunnelDisconnected(NetRelayLink link, TunnelDisconnectedPacket packet);

	void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet);

}
