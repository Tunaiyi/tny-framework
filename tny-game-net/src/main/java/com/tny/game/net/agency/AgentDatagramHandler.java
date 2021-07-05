package com.tny.game.net.agency;

import com.tny.game.net.agency.datagram.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 12:31 下午
 */
public interface AgentDatagramHandler {

    void onConnect(NetPipe<?> pipe, ConnectDatagram datagram);

    void onMessage(NetPipe<?> pipe, MessageDatagram datagram);

    void onDisconnect(NetPipe<?> pipe, DisconnectDatagram datagram);

    void onHeartBeat(NetPipe<?> pipe, HeartbeatDatagram datagram);

    void onDisconnected(NetPipe<?> pipe, DisconnectedDatagram datagram);

    void onConnected(NetPipe<?> pipe, ConnectedDatagram datagram);

}
