package com.tny.game.net.command.dispatcher;

import com.tny.game.net.command.*;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.transport.message.Message;
import org.springframework.stereotype.Component;

@Component
public class TestPluginAfter implements VoidControllerPlugin<Object> {

    @Override
    public void doExecute(Tunnel<Object> tunnel, Message<Object> message, InvokeContext context) {
        System.out.println("TestPluginAfter " + message.getProtocol());
    }

}
