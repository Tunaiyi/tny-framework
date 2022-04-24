package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * 通道事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 9:06 下午
 */
public interface TunnelPacket<A extends RelayPacketArguments> extends RelayPacket<A> {

    long getTunnelId();

}
