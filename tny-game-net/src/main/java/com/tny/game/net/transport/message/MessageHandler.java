package com.tny.game.net.transport.message;

import com.tny.game.net.transport.NetTunnel;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-22 03:39
 */
public interface MessageHandler<UID> {

    void handle(NetTunnel<UID> tunnel, NetMessage<UID> message);

}
