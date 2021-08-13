package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 8:27 下午
 */
public interface RelayPacketFactory<P extends RelayPacket<A>, A extends RelayPacketArguments> {

	P createPacket(long tunnelId, A arguments, long time);

}
