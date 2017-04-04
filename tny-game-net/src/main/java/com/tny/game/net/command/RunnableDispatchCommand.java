package com.tny.game.net.command;

/**
 * Created by Kun Yang on 16/6/15.
 */
public class RunnableDispatchCommand extends BaseDispatchCommand<Void> {

    private Runnable runnable;

    public RunnableDispatchCommand(Runnable runnable) {
        super("RunnableDispatcherCommand");
        this.runnable = runnable;
    }

    @Override
    public Void invoke() {
        this.runnable.run();
        return null;
    }
    
}
