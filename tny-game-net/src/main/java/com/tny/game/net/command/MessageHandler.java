package com.tny.game.net.command;

import com.tny.game.common.unit.annotation.UnitInterface;
import com.tny.game.net.message.NetMessage;
import com.tny.game.net.transport.NetTunnel;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-22 03:39
 */
@UnitInterface
public interface MessageHandler<UID> {

    void handle(NetTunnel<UID> tunnel, NetMessage<UID> message);

}
