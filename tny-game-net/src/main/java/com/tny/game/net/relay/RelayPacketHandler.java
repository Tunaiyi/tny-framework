package com.tny.game.net.relay;

import com.tny.game.net.relay.packet.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 12:31 下午
 */
public interface RelayPacketHandler {

	void onPipeOpen(NetRelayPipe<?> pipe, PipeOpenPacket packet);

	void onPipeClose(NetRelayPipe<?> pipe, PipeClosePacket packet);

	void onPipeHeartBeat(NetRelayPipe<?> pipe, PipeHeartBeatPacket packet);

	void onTubuleConnect(NetRelayPipe<?> pipe, TubuleConnectPacket packet);

	void onTubuleConnected(NetRelayPipe<?> pipe, TubuleConnectedPacket packet);

	void onTubuleDisconnect(NetRelayPipe<?> pipe, TubuleDisconnectPacket packet);

	void onTubuleDisconnected(NetRelayPipe<?> pipe, TubuleDisconnectedPacket packet);

	void onTubuleRelay(NetRelayPipe<?> pipe, TubuleMessagePacket packet);

}
