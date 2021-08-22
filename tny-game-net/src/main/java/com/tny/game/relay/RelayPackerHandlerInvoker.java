package com.tny.game.relay;

import com.tny.game.relay.exception.*;
import com.tny.game.relay.packet.*;
import com.tny.game.relay.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/25 8:07 下午
 */
@FunctionalInterface
public interface RelayPackerHandlerInvoker<D extends RelayPacket<?>> {

	void invoke(RelayPacketHandler handler, NetRelayLink link, D datagram) throws InvokeHandlerException;

}
