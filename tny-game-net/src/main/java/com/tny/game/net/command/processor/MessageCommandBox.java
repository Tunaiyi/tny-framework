/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.command.processor;

import com.tny.game.net.command.dispatcher.*;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;

import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:57 下午
 */
public class MessageCommandBox implements Executor {

    public static final Logger LOGGER = getLogger(MessageCommandBox.class);

    private final CommandExecutor executor;

    public MessageCommandBox(CommandExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        this.addRunnable(runnable);
    }

    public boolean addCommand(RpcEnterContext rpcContext) {
        return this.doAddCommand(rpcContext);
    }

    private RpcCommand createCommand(RpcEnterContext rpcContext) {
        var message = rpcContext.getMessage();
        switch (message.getMode()) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                var context = rpcContext.networkContext();
                MessageDispatcher dispatcher = context.getMessageDispatcher();
                return dispatcher.dispatch(rpcContext);
            case PING:
                var tunnel = rpcContext.netTunnel();
                rpcContext.complete();
                return new RunnableCommand(tunnel::pong);
            default:
                LOGGER.warn("unknown message mode {} absorbed on channel {}", message.getMode(), rpcContext.netTunnel());
        }
        rpcContext.complete();
        return null;
    }

    private boolean doAddCommand(RpcEnterContext rpcContext) {
        var command = createCommand(rpcContext);
        if (command == null) {
            // 未知模式已在 createCommand 内完成告警与上下文终结；null 命令不得入执行队列（三层 NPE 防线）
            return true;
        }
        executor.executeCommand(command);
        return true;
    }

    private void addRunnable(Runnable runnable) {
        executor.executeRunnable( runnable);
    }

}
