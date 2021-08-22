package com.tny.game.relay;

import com.tny.game.relay.packet.*;

/**
 * <p>e
 *
 * @author : kgtny
 * @date : 2021/5/21 3:32 下午
 */
public interface RelayPacketMaker {

	RelayPacket<?> make();

}
