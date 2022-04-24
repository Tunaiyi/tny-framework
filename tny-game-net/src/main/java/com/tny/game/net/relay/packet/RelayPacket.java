package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * 通道事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 9:06 下午
 */
public interface RelayPacket<A extends RelayPacketArguments> {

    static void release(RelayPacket<?> packet) {
        if (packet == null) {
            return;
        }
        RelayPacketArguments arguments = packet.getArguments();
        if (arguments != null) {
            arguments.release();
        }
    }

    int getId();

    RelayPacketType getType();

    long getTime();

    A getArguments();

}
