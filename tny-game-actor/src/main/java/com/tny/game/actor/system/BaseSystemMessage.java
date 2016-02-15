package com.tny.game.actor.system;

import com.tny.game.actor.SystemMessage;

class BaseSystemMessage implements SystemMessage {

	protected SystemMessageType messageType;

	protected BaseSystemMessage(SystemMessageType messageType) {
		super();
		this.messageType = messageType;
	}

	@Override
	public SystemMessageType messageType() {
		return messageType;
	}

}
