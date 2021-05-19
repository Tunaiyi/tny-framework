package com.tny.game.net.command;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-22 03:39
 */
public interface MessageHandler<UID> {

    void handle(NetTunnel<UID> tunnel, Message message, RespondFuture future);

}
