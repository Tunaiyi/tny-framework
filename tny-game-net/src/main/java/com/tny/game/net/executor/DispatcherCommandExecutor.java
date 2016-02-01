package com.tny.game.net.executor;

import com.tny.game.net.dispatcher.DispatcherCommand;

/**
 * @author KGTny
 */
public interface DispatcherCommandExecutor {

    public void shutdown();

    public void sumit(DispatcherCommand<?> command);

}
