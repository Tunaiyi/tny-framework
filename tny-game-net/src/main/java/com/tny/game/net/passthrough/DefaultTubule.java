package com.tny.game.net.passthrough;

import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/28 11:26 下午
 */
public class DefaultTubule<UID> extends BaseTubule<UID> {

    protected DefaultTubule(long id, TunnelMode mode, NetPipe<UID> pipe, InetSocketAddress remoteAddress,
            NetBootstrapContext<UID> bootstrapContext) {
        super(id, mode, pipe, remoteAddress, bootstrapContext);
    }

}
