package com.tny.game.net.command;

import com.tny.game.common.worker.command.Command;
import com.tny.game.net.tunnel.NetTunnel;

/**
 * @author KGTny
 */
public interface DispatchCommandExecutor {

    void shutdown();

    void submit(NetTunnel<?> tunnel, Command command);

}
