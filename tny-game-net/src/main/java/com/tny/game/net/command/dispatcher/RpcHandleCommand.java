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

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/14 17:52
 **/
public abstract class RpcHandleCommand implements RpcCommand {

    protected final RpcProviderContext rpcContext;

    private boolean start;

    public RpcHandleCommand(RpcProviderContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    public RpcProviderContext getRpcContext() {
        return rpcContext;
    }

    @Override
    public void execute() {
        Throwable cause = null;
        try {
            if (isDone()) {
                return;
            }
            RpcContexts.setCurrent(rpcContext);
            this.onTickStart();
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
            cause = e;
            this.onException(e);
        } finally {
            try {
                onTickEnd(cause);
                if (this.isDone()) {
                    onDone(cause);
                }
            } finally {
                RpcContexts.clear();
            }
        }
    }

    protected abstract void onTickStart();

    protected abstract void onRun() throws Exception;

    protected abstract void onDone(Throwable cause);

    protected abstract void onTick();

    protected abstract void onTickEnd(Throwable cause);

    protected abstract void onException(Throwable cause);

}
