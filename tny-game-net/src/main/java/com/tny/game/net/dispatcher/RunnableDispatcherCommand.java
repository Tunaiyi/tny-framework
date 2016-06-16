package com.tny.game.net.dispatcher;

/**
 * Created by Kun Yang on 16/6/15.
 */
public class RunnableDispatcherCommand extends BaseDispatcherCommand<Void> {

    private Runnable runnable;

    public RunnableDispatcherCommand(Runnable runnable) {
        super("RunnableDispatcherCommand");
        this.runnable = runnable;
    }

    @Override
    public Void invoke() {
        this.runnable.run();
        return null;
    }
    
}
