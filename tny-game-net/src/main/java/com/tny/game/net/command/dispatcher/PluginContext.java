package com.tny.game.net.command.dispatcher;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

public class PluginContext {

    public static final Logger LOGGER = LoggerFactory.getLogger(PluginContext.class);

    private PluginContext nextContext;

    private ControllerPluginHolder plugin;

    public PluginContext(ControllerPluginHolder plugin) {
        this.plugin = plugin;
        this.nextContext = null;
    }

    @SuppressWarnings("unchecked")
    public void execute(Tunnel<?> tunnel, Message<?> message, CommandContext context) {
        if (this.plugin == null || context.isIntercept())
            return;
        try {
            this.plugin.invokePlugin(tunnel, message, context);
        } catch (Throwable e) {
            LOGGER.error("invoke plugin {} exception", this.plugin.getClass(), e);
        }
        if (this.nextContext == null || context.isIntercept())
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
