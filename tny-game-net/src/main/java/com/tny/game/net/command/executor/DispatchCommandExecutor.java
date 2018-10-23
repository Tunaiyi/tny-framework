package com.tny.game.net.command.executor;

import com.tny.game.common.worker.command.Command;
import com.tny.game.net.transport.NetTunnel;

/**
 * @author KGTny
 */
public interface DispatchCommandExecutor {

    /**
     * 提交命令
     *
     * @param tunnel  客户端
     * @param command 命令
     */
    void submit(NetTunnel<?> tunnel, Command command);

    /**
     * 关闭
     */
    void shutdown();

}
