package com.tny.game.net.executor;

import com.tny.game.net.dispatcher.DispatcherCommand;
import com.tny.game.net.session.Session;

/**
 * @author KGTny
 */
public interface DispatcherCommandExecutor {

    void shutdown();

    void submit(Session session, DispatcherCommand<?> command);

}
