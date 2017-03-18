package com.tny.game.net.dispatcher;

import com.tny.game.net.plugin.ControllerPlugin;
import com.tny.game.net.plugin.PluginContext;
import org.springframework.stereotype.Component;

@Component
public class TestPluginAfter implements ControllerPlugin {

    @Override
    public CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception {
        System.out.println("TestPluginAfter " + request.getProtocol());
        return context.passToNext(request, result);
    }

}
