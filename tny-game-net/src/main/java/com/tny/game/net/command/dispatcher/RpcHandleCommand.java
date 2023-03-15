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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.concurrent.worker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/14 17:52
 **/
public abstract class RpcHandleCommand implements RpcCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcHandleCommand.class);

    protected final RpcEnterContext enterContext;

    protected CompletableFuture<Object> future;

    private boolean start;

    public RpcHandleCommand(RpcEnterContext rpcContext) {
        this.enterContext = rpcContext;
    }

    @Override
    public CompletableFuture<Object> execute(AsyncWorker execute) {
        Throwable cause = null;
        try {
            if (isDone()) {
                return CompletableFuture.completedFuture(null);
            }
            RpcContexts.setCurrent(enterContext);
            if (!this.start) {
                try {
                    // 调用逻辑业务
                    this.doExecute(execute);
                } finally {
                    this.start = true;
                }
            }
            return future;
        } catch (Throwable e) {
            LOGGER.error("{} execute exception", this.getName(), e);
            cause = e;
            return future;
        } finally {
            try {
                if (this.isDone()) {
                    onDone(cause);
                }
            } finally {
                RpcContexts.clear();
            }
        }
    }

    protected abstract boolean isDone();

    protected abstract void doExecute(AsyncWorker execute) throws Exception;

    protected abstract void doDone();

    private void onDone() {
        this.onDone(null);
    }

    protected void onDone(Throwable cause) {
        RpcContexts.setCurrent(enterContext);
        try {
            if (cause != null) {
                LOGGER.error("execute {} exception", getName(), cause);
                onException(cause);
            }
            this.doDone();
        } finally {
            RpcContexts.clear();
        }
    }

    protected abstract void onException(Throwable cause);

}
