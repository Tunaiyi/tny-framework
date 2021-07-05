package com.tny.game.net.agency;

import com.tny.game.net.agency.datagram.*;
import com.tny.game.net.agency.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/25 8:07 下午
 */
@FunctionalInterface
public interface AgentDatagramHandlerInvoker<D extends TubuleDatagram> {

    void invoke(AgentDatagramHandler handler, NetPipe<?> pipe, D datagram) throws InvokeHandlerException;

}
