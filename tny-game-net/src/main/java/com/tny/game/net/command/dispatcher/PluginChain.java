package com.tny.game.net.command.dispatcher;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

public class PluginChain {

	public static final Logger LOGGER = LoggerFactory.getLogger(PluginChain.class);

	private PluginChain next;

	private CommandPluginHolder plugin;

	public PluginChain(CommandPluginHolder plugin) {
		this.plugin = plugin;
		this.next = null;
	}

	@SuppressWarnings("unchecked")
	public void execute(Tunnel<?> tunnel, Message message, MessageCommandContext context) {
		if (this.plugin == null || context.isIntercept()) {
			return;
		}
		try {
			this.plugin.invokePlugin(tunnel, message, context);
		} catch (Throwable e) {
			LOGGER.error("invoke plugin {} exception", this.plugin.getClass(), e);
		}
		if (this.next == null || context.isIntercept()) {
			return;
		}
		this.next.execute(tunnel, message, context);
	}

	public void append(PluginChain chain) {
		if (this.next == null || this.next.plugin == null) {
			this.next = chain;
		} else {
			this.next.append(chain);
		}
	}

}
