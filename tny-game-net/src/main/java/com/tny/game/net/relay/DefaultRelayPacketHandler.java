package com.tny.game.net.relay;

import com.tny.game.net.relay.exception.*;
import com.tny.game.net.relay.packet.*;
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
    public void onConnect(NetPipe<?> pipe, ConnectPacket packet) {
        try {
            pipe.connectTubule(packet.getTunnelId(), packet.getIp(), packet.getPort());
        } catch (PipeClosedException e) {
            pipe.getTransmitter().write(new DisconnectedPacket(packet.getTunnelId()), null);
        }
    }

    @Override
    public void onMessage(NetPipe<?> pipe, MessagePacket packet) {
        NetTubule<?> tubule = pipe.getTubule(packet.getTunnelId());
        if (tubule == null) {
            pipe.closeTubule(packet.getTunnelId());
        } else {
            tubule.receive(packet.getMessage());
        }
    }

    @Override
    public void onDisconnect(NetPipe<?> pipe, DisconnectPacket packet) {
        pipe.closeTubule(packet.getTunnelId());
    }

    @Override
    public void onHeartBeat(NetPipe<?> pipe, HeartbeatPacket packet) {
        NetTubule<?> tubule = pipe.getTubule(packet.getTunnelId());
        if (tubule == null) {
            pipe.closeTubule(packet.getTunnelId());
        } else {
            tubule.pong();
        }
    }

    @Override
    public void onDisconnected(NetPipe<?> pipe, DisconnectedPacket packet) {
        LOGGER.warn("{} 无法处理Disconnected事件 {}", pipe, packet);
    }

    @Override
    public void onConnected(NetPipe<?> pipe, ConnectedPacket packet) {
    }

}
