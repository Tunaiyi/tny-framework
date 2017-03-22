package com.tny.game.net.netty;

import com.tny.game.net.common.handle.ImmediateOutputEventHandler;
import com.tny.game.net.session.event.SessionSendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Kun Yang on 2017/3/20.
 */
public class NettyImmediateOutputEventHandler<UID> extends ImmediateOutputEventHandler<UID, NettySession<UID>> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyImmediateOutputEventHandler.class);

    @Override
    protected void write(NettySession<UID> session, SessionSendEvent event) {
        session.write(event);
    }
}
