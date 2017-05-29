package com.tny.game.net.command;

import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginContext {

    public static final Logger LOGGER = LoggerFactory.getLogger(PluginContext.class);

    private PluginContext nextContext;

    private ControllerPlugin plugin;

    public PluginContext(ControllerPlugin plugin) {
        this.plugin = plugin;
        this.nextContext = null;
    }

    @SuppressWarnings("unchecked")
    public void execute(Tunnel<?> tunnel, Message<?> message, InvokeContext context) {
        if (this.plugin == null || context.isInterrupted())
            return;
        try {
            this.plugin.execute(tunnel, message, context);
        } catch (Throwable e) {
            LOGGER.error("invoke plugin {} exception", this.plugin.getClass());
        }
        if (this.nextContext == null || context.isInterrupted())
            return;
        this.nextContext.execute(tunnel, message, context);
    }

    public void setNext(PluginContext nextPlugin) {
        if (this.nextContext == null || this.nextContext.plugin == null) {
            this.nextContext = nextPlugin;
        } else {
            this.nextContext.setNext(nextPlugin);
        }
    }

}
