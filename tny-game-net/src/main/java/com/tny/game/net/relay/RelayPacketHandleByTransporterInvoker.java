package com.tny.game.net.relay;

import com.tny.game.net.relay.exception.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/25 8:07 下午
 */
@FunctionalInterface
public interface RelayPacketHandleByTransporterInvoker<D extends RelayPacket<?>> {

	void invoke(RelayPacketProcessor handler, NetRelayTransporter transporter, D datagram) throws InvokeHandlerException;

}
