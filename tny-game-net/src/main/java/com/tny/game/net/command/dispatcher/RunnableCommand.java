package com.tny.game.net.command.dispatcher;

import com.tny.game.common.worker.command.*;

/**
 * Created by Kun Yang on 16/6/15.
 */
public class RunnableCommand extends BaseCommand {

	private final Runnable runnable;

	public RunnableCommand(Runnable runnable) {
		super("RunnableCommand");
		this.runnable = runnable;
	}

	@Override
	protected void action() {
		this.runnable.run();
	}

	@Override
	public String getName() {
		return this.runnable.getClass().getCanonicalName();
	}

}

