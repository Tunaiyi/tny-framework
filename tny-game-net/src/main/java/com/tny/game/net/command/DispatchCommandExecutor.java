package com.tny.game.net.command;

import com.tny.game.net.session.Session;

/**
 * @author KGTny
 */
public interface DispatchCommandExecutor {

    void shutdown();

    void submit(Session session, DispatchCommand<?> command);

}
