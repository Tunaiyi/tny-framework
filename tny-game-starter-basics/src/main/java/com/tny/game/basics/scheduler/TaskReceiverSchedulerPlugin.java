package com.tny.game.basics.scheduler;

import com.tny.game.basics.configuration.*;
import com.tny.game.common.scheduler.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;
import org.springframework.stereotype.Component;

@Component
public class TaskReceiverSchedulerPlugin implements VoidCommandPlugin<Long> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskReceiverSchedulerPlugin.class);

	private BasicsTimeTaskProperties properties;

	private TimeTaskService timeTaskService;

	public TaskReceiverSchedulerPlugin(BasicsTimeTaskProperties properties, TimeTaskService timeTaskService) {
		this.properties = properties;
		this.timeTaskService = timeTaskService;
	}

	@Override
	public void doExecute(Tunnel<Long> tunnel, Message message, MessageCommandContext context) throws Exception {
		TaskReceiverType type = properties.getPlugin().getReceiverType(tunnel.getUserType());
		if (type != null) {
			try {
				this.timeTaskService.checkTask(tunnel.getUserId(), type);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
	}

}
