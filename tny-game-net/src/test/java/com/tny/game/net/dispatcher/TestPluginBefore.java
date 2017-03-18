package com.tny.game.net.dispatcher;

import com.tny.game.net.plugin.ControllerPlugin;
import com.tny.game.net.plugin.PluginContext;
import org.springframework.stereotype.Component;

@Component
public class TestPluginBefore implements ControllerPlugin {

    @Override
    public CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception {
        System.out.println("TestPluginBefore " + request.getProtocol());
        return context.passToNext(request, result);
    }

}
