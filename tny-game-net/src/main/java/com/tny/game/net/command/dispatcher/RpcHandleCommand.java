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

import org.slf4j.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/14 17:52
 **/
public abstract class RpcHandleCommand implements RpcCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcHandleCommand.class);

    protected final RpcEnterContext rpcContext;

    private boolean start;

    public RpcHandleCommand(RpcEnterContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    @Override
    public void execute() {
        Throwable cause = null;
        try {
            if (isDone()) {
                return;
            }
            RpcContexts.setCurrent(rpcContext);
            if (!this.start) {
                try {
                    // 调用逻辑业务
                    this.onRun();
                } finally {
                    this.start = true;
                }
            }
            if (!this.isDone()) {
                this.onTick();
            }
        } catch (Throwable e) {
            LOGGER.error("{} execute exception", this.getName(), e);
            cause = e;
            this.onException(e);
        } finally {
            try {
                onEndTick(cause);
                if (this.isDone()) {
                    onDone(cause);
                }
            } finally {
                RpcContexts.clear();
            }
        }
    }

    protected abstract void onStartTick();

    protected abstract void onRun() throws Exception;

    protected abstract void onDone(Throwable cause);

    protected abstract void onTick();

    protected abstract void onEndTick(Throwable cause);

    protected abstract void onException(Throwable cause);

}
