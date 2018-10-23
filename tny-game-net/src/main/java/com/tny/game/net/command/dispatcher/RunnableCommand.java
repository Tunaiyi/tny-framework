package com.tny.game.net.command.dispatcher;

import com.tny.game.common.worker.command.BaseCommand;

/**
 * Created by Kun Yang on 16/6/15.
 */
public class RunnableCommand extends BaseCommand {

    private Runnable runnable;

    public RunnableCommand(Runnable runnable) {
        super("RunnableDispatcherCommand");
        this.runnable = runnable;
    }

    @Override
    protected void action() {
        this.runnable.run();
    }

    @Override
    public String getName() {
        return runnable.getClass().getCanonicalName();
    }
}

