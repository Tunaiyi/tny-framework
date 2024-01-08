/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
        }
        rpcContext.complete();
        return null;
    }

    private boolean doAddCommand(RpcEnterContext rpcContext) {
        var command = createCommand(rpcContext);
        executor.executeCommand(command);
        return true;
    }

    private void addRunnable(Runnable runnable) {
        executor.executeRunnable( runnable);
    }

}
