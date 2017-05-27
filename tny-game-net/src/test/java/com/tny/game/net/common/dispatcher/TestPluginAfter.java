package com.tny.game.net.common.dispatcher;

import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.InvokeContext;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.springframework.stereotype.Component;

@Component
public class TestPluginAfter implements ControllerPlugin {

    @Override
    public void execute(Tunnel tunnel, Message message, InvokeContext context) throws Exception {
        System.out.println("TestPluginAfter " + message.getProtocol());
    }

}
