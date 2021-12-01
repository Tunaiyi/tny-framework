package com.tny.game.basics.scheduler;

import com.tny.game.basics.item.*;
import com.tny.game.common.scheduler.*;

public class GameTaskReceiver extends TaskReceiver implements Any {

	protected long playerId;

	protected GameTaskReceiver() {
	}

	@Override
	public long getId() {
		return playerId;
	}

	@Override
	public long getPlayerId() {
		return this.playerId;
	}

	protected void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	protected void setType(TaskReceiverType type) {
		this.type = type;
	}

	public void setActualLastHandlerTime(long actualLastHandlerTime) {
		this.actualLastHandleTime = actualLastHandlerTime;
	}

	protected void setLastHandlerTime(long lastHandlerTime) {
		this.lastHandlerTime = lastHandlerTime;
	}

}
