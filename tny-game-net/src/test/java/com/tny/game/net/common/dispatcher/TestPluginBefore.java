package com.tny.game.net.common.dispatcher;

import com.tny.game.net.command.CommandResult;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.PluginContext;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.springframework.stereotype.Component;

@Component
public class TestPluginBefore implements ControllerPlugin {


    @Override
    public CommandResult execute(Tunnel tunnel, Message message, CommandResult result, PluginContext context) throws Exception {
        System.out.println("TestPluginBefore " + message.getProtocol());
        return context.passToNext(tunnel, message, result);
    }
}
