package com.tny.game.net.command.executor;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.endpoint.task.*;
import com.tny.game.net.transport.*;

import java.util.function.Consumer;

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
     * 立即调度
     *
     * @param box 调度事件箱
     */
    void submit(EndpointCommandTaskBox box);

    /**
     * 延迟调度
     *
     * @param handle 调度事件箱
     */
    void schedule(EndpointCommandTaskBox box, Consumer<EndpointCommandTaskBox> handle);

    /**
     * 关闭
     */
    void shutdown();

    /**
     * @return 是否停止
     */
    boolean isShutdown();

}
