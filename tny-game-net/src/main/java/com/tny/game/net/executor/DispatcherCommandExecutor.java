package com.tny.game.net.executor;

import com.tny.game.net.dispatcher.DispatcherCommand;

/**
 * @author KGTny
 */
public interface DispatcherCommandExecutor {

    void shutdown();

    void submit(DispatcherCommand<?> command);

}
