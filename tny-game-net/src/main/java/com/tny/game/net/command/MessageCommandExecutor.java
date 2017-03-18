package com.tny.game.net.command;

import com.tny.game.net.session.Session;

/**
 * @author KGTny
 */
public interface MessageCommandExecutor {

    void shutdown();

    void submit(Session session, MessageCommand<?> command);

}
