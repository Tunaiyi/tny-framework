package com.tny.game.net.plugin;

import com.tny.game.net.message.Message;
import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.MethodControllerHolder;

public class PluginContext {

    private final static PluginContext EMPTY_PLUGIN = new PluginContext(null);

    private PluginContext nextContext;

    private ControllerPlugin plugin;

    private MethodControllerHolder methodHolder;

    public PluginContext(MethodControllerHolder methodHolder, ControllerPlugin plugin) {
        this.plugin = plugin;
        this.methodHolder = methodHolder;
        this.nextContext = new PluginContext(methodHolder);
    }

    private PluginContext(MethodControllerHolder methodHolder) {
        this.methodHolder = methodHolder;
    }

    public MethodControllerHolder getMethodHolder() {
        return this.methodHolder;
    }

    @SuppressWarnings("unchecked")
    public CommandResult passToNext(Message<?> message, CommandResult result) throws Exception {
        if (this.plugin == null)
            return result;
        return this.plugin.execute(message, result, this.nextContext != null ? this.nextContext : EMPTY_PLUGIN);
    }

    public void setNext(PluginContext nextPlugin) {
        if (this.nextContext == null || this.nextContext.plugin == null) {
            this.nextContext = nextPlugin;
        } else {
            this.nextContext.setNext(nextPlugin);
        }
    }

}
