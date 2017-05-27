package com.tny.game.net.command;

import com.tny.game.net.session.Session;
import com.tny.game.worker.command.Command;

/**
 * @author KGTny
 */
public interface DispatchCommandExecutor {

    void shutdown();

    void submit(Session session, Command command);

}
