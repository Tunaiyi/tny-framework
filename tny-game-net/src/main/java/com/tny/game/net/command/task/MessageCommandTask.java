package com.tny.game.net.command.task;

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
public class MessageCommandTask implements CommandTask {

	private final Message message;

	private final NetTunnel<?> tunnel;

	private final MessageDispatcher messageDispatcher;

	public MessageCommandTask(NetTunnel<?> tunnel, Message message, MessageDispatcher messageDispatcher) {
		this.message = message;
		this.messageDispatcher = messageDispatcher;
		this.tunnel = tunnel;
	}

	@Override
	public Command createCommand() {
		switch (this.message.getMode()) {
			case PUSH:
			case REQUEST:
			case RESPONSE:
				return this.messageDispatcher.dispatch(this.tunnel, this.message);
			case PING:
				return new RunnableCommand(this.tunnel::pong);
			default:
		}
		return null;
	}

}
