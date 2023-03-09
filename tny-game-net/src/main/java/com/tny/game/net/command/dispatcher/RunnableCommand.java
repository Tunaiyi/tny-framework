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

import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 16/6/15.
 */
public class RunnableCommand implements RpcCommand {

    private final Runnable runnable;

    public RunnableCommand(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public CompletableFuture<Object> execute(AsyncWorker worker) {
        this.runnable.run();
        return null;
    }

    @Override
    public String getName() {
        return this.runnable.getClass().getCanonicalName();
    }

}

