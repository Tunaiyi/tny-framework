package com.tny.game.net.endpoint.task;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:33 下午
 */
public class RespondCommandTask implements CommandTask {

	private final Message message;

	private final MessageRespondAwaiter respondFuture;

	public RespondCommandTask(Message message, MessageRespondAwaiter respondFuture) {
		this.message = message;
		this.respondFuture = respondFuture;
	}

	@Override
	public Command createCommand() {
		return new RespondFutureCommand(this.message, this.respondFuture);
	}

}
