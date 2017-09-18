package com.tny.game.net.tunnel;

import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageWriteFuture;
import com.tny.game.net.session.NetSession;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface NetTunnel<UID> extends Tunnel<UID> {

    boolean bind(NetSession<UID> session);

    void ping();

    void pong();

    void write(Message<UID> message, MessageWriteFuture<UID> writeFuture) throws Throwable;

}
