package com.tny.game.net.relay;

import com.tny.game.net.relay.packet.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 12:31 下午
 */
public interface RelayPacketHandler {

    void onConnect(NetPipe<?> pipe, ConnectPacket packet);

    void onMessage(NetPipe<?> pipe, MessagePacket packet);

    void onDisconnect(NetPipe<?> pipe, DisconnectPacket packet);

    void onHeartBeat(NetPipe<?> pipe, HeartbeatPacket packet);

    void onDisconnected(NetPipe<?> pipe, DisconnectedPacket packet);

    void onConnected(NetPipe<?> pipe, ConnectedPacket packet);

}
