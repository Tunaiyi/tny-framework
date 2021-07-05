package com.tny.game.net.agency;

import com.tny.game.net.agency.datagram.*;
import com.tny.game.net.agency.exception.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public class DefaultAgentDatagramHandler implements AgentDatagramHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultAgentDatagramHandler.class);

    @Override
    public void onConnect(NetPipe<?> pipe, ConnectDatagram datagram) {
        try {
            pipe.connectTubule(datagram.getTunnelId(), datagram.getIp(), datagram.getPort());
        } catch (PipeClosedException e) {
            pipe.getTransmitter().write(new DisconnectedDatagram(datagram.getTunnelId()), null);
        }
    }

    @Override
    public void onMessage(NetPipe<?> pipe, MessageDatagram datagram) {
        NetTubule<?> tubule = pipe.getTubule(datagram.getTunnelId());
        if (tubule == null) {
            pipe.closeTubule(datagram.getTunnelId());
        } else {
            tubule.receive(datagram.getMessage());
        }
    }

    @Override
    public void onDisconnect(NetPipe<?> pipe, DisconnectDatagram datagram) {
        pipe.closeTubule(datagram.getTunnelId());
    }

    @Override
    public void onHeartBeat(NetPipe<?> pipe, HeartbeatDatagram datagram) {
        NetTubule<?> tubule = pipe.getTubule(datagram.getTunnelId());
        if (tubule == null) {
            pipe.closeTubule(datagram.getTunnelId());
        } else {
            tubule.pong();
        }
    }

    @Override
    public void onDisconnected(NetPipe<?> pipe, DisconnectedDatagram datagram) {
        LOGGER.warn("{} 无法处理Disconnected事件 {}", pipe, datagram);
    }

    @Override
    public void onConnected(NetPipe<?> pipe, ConnectedDatagram datagram) {
    }

}
