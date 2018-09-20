package com.tny.game.net.command;

import com.tny.game.net.transport.message.Message;
import com.tny.game.net.transport.Tunnel;

/**
 * Created by Kun Yang on 2017/5/29.
 */
public class NonePlugin<UID> implements ControllerPlugin<UID> {

    @Override
    public void execute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) throws Exception {
    }

}
