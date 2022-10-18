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
package com.tny.game.common.concurrent.worker;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 单线 worker 执行器
 * worker 同一时间只会提交一次到 masterExecutor, 知道 masterExecutor 执行完 worker 后, worker 才可以再次提交.
 * <p>
 *
 * @author kgtny
 * @date 2022/6/13 04:43
 **/
public class SingleWorkerExecutor extends SerialWorkerExecutor {

    public SingleWorkerExecutor(Executor masterExecutor) {
        super(masterExecutor);
    }

    public SingleWorkerExecutor(Executor masterExecutor, boolean immediatelyInExecutor) {
        super(masterExecutor, immediatelyInExecutor);
    }

    public SingleWorkerExecutor(Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean unsafeQueue) {
        super(masterExecutor, taskQueue, unsafeQueue);
    }

    public SingleWorkerExecutor(Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean immediatelyInExecutor, boolean unsafeQueue) {
        super(masterExecutor, taskQueue, immediatelyInExecutor, unsafeQueue);
    }

    @Override
    public CompletableFuture<Void> awaitDelay(Runnable runnable, int delayTime, TimeUnit timeUnit) {
        var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit);
        return await(() -> CompletableFuture.runAsync(NOOP, delay))
                .thenAccept((v) -> runnable.run());
    }

    @Override
    public <T> CompletableFuture<T> awaitDelay(Supplier<T> supplier, int delayTime, TimeUnit timeUnit) {
        var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this);
        return await(() -> CompletableFuture.runAsync(NOOP, delay))
                .thenApply((v) -> supplier.get());
    }

    @Override
    public CompletableFuture<Void> delay(Runnable runnable, int delayTime, TimeUnit timeUnit) {
        var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this);
        return CompletableFuture.runAsync(runnable, delay);
    }

    @Override
    public <T> CompletableFuture<T> delay(Supplier<T> supplier, int delayTime, TimeUnit timeUnit) {
        var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this);
        return CompletableFuture.supplyAsync(supplier, delay);
    }

}