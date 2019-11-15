package com.tny.game.net.command.executor;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.transport.*;

/**
 * @author KGTny
 */
@UnitInterface
public interface MessageCommandExecutor {

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
