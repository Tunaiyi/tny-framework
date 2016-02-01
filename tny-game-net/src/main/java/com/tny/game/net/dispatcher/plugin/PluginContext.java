package com.tny.game.net.dispatcher.plugin;

import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.dispatcher.Request;

public class PluginContext {

    private final static PluginContext EMPTY_PLUGIN = new PluginContext(null);

    private PluginContext nextContext;

    private ControllerPlugin plugin;

    private MethodHolder methodHolder;

    public PluginContext(MethodHolder methodHolder, ControllerPlugin plugin) {
        this.plugin = plugin;
        this.methodHolder = methodHolder;
        this.nextContext = new PluginContext(methodHolder);
    }

    private PluginContext(MethodHolder methodHolder) {
        this.methodHolder = methodHolder;
    }

    public MethodHolder getMethodHolder() {
        return this.methodHolder;
    }

    public CommandResult passToNext(Request request, CommandResult result) throws Exception {
        if (this.plugin == null)
            return result;
        return this.plugin.execute(request, result, this.nextContext != null ? this.nextContext : EMPTY_PLUGIN);
    }

    public void setNext(PluginContext nextPlugin) {
        if (this.nextContext == null || this.nextContext.plugin == null) {
            this.nextContext = nextPlugin;
        } else {
            this.nextContext.setNext(nextPlugin);
        }
    }

}
