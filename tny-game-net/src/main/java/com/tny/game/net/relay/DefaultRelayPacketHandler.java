package com.tny.game.net.relay;

import com.tny.game.net.relay.exception.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public class DefaultRelayPacketHandler implements RelayPacketHandler {

	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultRelayPacketHandler.class);

	@Override
	public void onPipeOpen(NetRelayPipe<?> pipe, PipeOpenPacket packet) {
	}

	@Override
	public void onPipeClose(NetRelayPipe<?> pipe, PipeClosePacket packet) {
	}

	@Override
	public void onTubuleConnect(NetRelayPipe<?> pipe, TubuleConnectPacket packet) {
		try {
			TubuleConnectArguments arguments = packet.getArguments();
			pipe.connectTubule(packet.getTunnelId(), arguments.getIp(), arguments.getPort());
		} catch (PipeClosedException e) {
			pipe.getTransmitter().write(new TubuleDisconnectedPacket(packet.getTunnelId()), null);
		}
	}

	@Override
	public void onTubuleRelay(NetRelayPipe<?> pipe, TubuleMessagePacket packet) {
		NetRelayTubule<?> tubule = pipe.getTubule(packet.getTunnelId());
		if (tubule == null) {
			pipe.closeTubule(packet.getTunnelId());
		} else {
			tubule.receive(packet.getArguments().getMessage());
		}
	}

	@Override
	public void onTubuleDisconnect(NetRelayPipe<?> pipe, TubuleDisconnectPacket packet) {
		pipe.closeTubule(packet.getTunnelId());
	}

	@Override
	public void onPipeHeartBeat(NetRelayPipe<?> pipe, PipeHeartBeatPacket packet) {
		NetRelayTubule<?> tubule = pipe.getTubule(packet.getTunnelId());
		if (tubule == null) {
			pipe.closeTubule(packet.getTunnelId());
		} else {
			tubule.pong();
		}
	}

	@Override
	public void onTubuleDisconnected(NetRelayPipe<?> pipe, TubuleDisconnectedPacket packet) {
		LOGGER.warn("{} 无法处理Disconnected事件 {}", pipe, packet);
	}

	@Override
	public void onTubuleConnected(NetRelayPipe<?> pipe, TubuleConnectedPacket packet) {
	}

}
