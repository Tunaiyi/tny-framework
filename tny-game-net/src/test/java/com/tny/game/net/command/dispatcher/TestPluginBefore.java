package com.tny.game.net.command.dispatcher;

import com.tny.game.net.command.*;
import com.tny.game.net.transport.message.Message;
import com.tny.game.net.transport.Tunnel;
import org.springframework.stereotype.Component;

@Component
public class TestPluginBefore implements VoidControllerPlugin<Object> {

    @Override
    public void doExecute(Tunnel<Object> tunnel, Message<Object> message, InvokeContext context) throws Exception {
        System.out.println("TestPluginBefore " + message.getProtocol());
    }

}

