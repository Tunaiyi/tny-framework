package com.tny.game.net.relay;

import com.tny.game.net.relay.exception.*;
import com.tny.game.net.relay.packet.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/25 8:07 下午
 */
@FunctionalInterface
public interface RelayPackerHandlerInvoker<D extends RelayPacket> {

    void invoke(RelayPacketHandler handler, NetPipe<?> pipe, D datagram) throws InvokeHandlerException;

}
