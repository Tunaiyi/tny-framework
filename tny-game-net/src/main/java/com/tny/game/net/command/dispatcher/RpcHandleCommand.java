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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.concurrent.worker.*;
import org.slf4j.*;

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
                // "至多一次"由上下文 CAS 终态机保证；"至少一次"由本守卫补全：
                // 异常且未达终态时强制走 onDone(cause)→onException→收尾，请求不得无终答悬挂
                // （command-execution"每个请求恰好收到一个终答"契约）
                if (this.isDone() || cause != null) {
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
