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
 * <p>
 *
 * @author kgtny
 * @date 2022/7/27 20:39
 **/
public interface AsyncWorker extends Executor {

    /**
     * 创建单线程异步Worker,
     *
     * @param masterExecutor worker执行器
     * @return 返回创建 worker.
     */
    static AsyncWorker createSingleWorker(Executor masterExecutor) {
        return new DefaultSingleAsyncWorker(masterExecutor);
    }

    /**
     * 创建单线程异步Worker,
     *
     * @param masterExecutor    worker执行器
     * @param taskQueue         任务队列
     * @param threadUnsafeQueue 是否是线程安全
     * @return 返回创建 worker.
     */
    static AsyncWorker createSingleWorker(Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean threadUnsafeQueue) {
        return new DefaultSingleAsyncWorker(masterExecutor, taskQueue, threadUnsafeQueue);
    }

    /**
     * 创建单线程异步Worker,且串行完成任务.
     *
     * @param masterExecutor worker执行器
     * @return 返回创建 worker.
     */
    static SerialAsyncWorker createSerialWorker(Executor masterExecutor) {
        return new DefaultSerialAsyncWorker(masterExecutor);
    }

    /**
     * 创建单线程异步Worker,且串行完成任务.
     *
     * @param masterExecutor    worker执行器
     * @param taskQueue         任务队列
     * @param threadUnsafeQueue 是否是线程安全
     * @return 返回创建 worker.
     */
    static SerialAsyncWorker createSerialWorker(Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean threadUnsafeQueue) {
        return new DefaultSerialAsyncWorker(masterExecutor, taskQueue, threadUnsafeQueue);
    }

    Runnable NOOP = () -> {
    };

    <T> CompletableFuture<T> apply(Supplier<T> runnable);

    <T> CompletableFuture<T> apply(Supplier<T> runnable, long timeout);

    <T> CompletableFuture<T> apply(Supplier<T> runnable, long timeout, TimeUnit unit);

    CompletableFuture<Void> run(Runnable runnable);

    CompletableFuture<Void> run(Runnable runnable, long timeout);

    CompletableFuture<Void> run(Runnable runnable, long timeout, TimeUnit unit);

    /**
     * 非阻塞延迟执行
     *
     * @param delayTime 延迟时间
     * @return 返回 future
     */
    default CompletableFuture<Void> delay(int delayTime) {
        return delay(delayTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 非阻塞延迟执行
     *
     * @param delayTime 延迟时间
     * @param timeUnit  延迟单位
     * @return 返回 future
     */
    default CompletableFuture<Void> delay(int delayTime, TimeUnit timeUnit) {
        return delay(NOOP, delayTime, timeUnit);
    }

    /**
     * 非阻塞延迟执行
     *
     * @param runnable  执行任务
     * @param delayTime 延迟时间
     * @param timeUnit  延迟单位
     * @return 返回 future
     */
    CompletableFuture<Void> delay(Runnable runnable, int delayTime, TimeUnit timeUnit);

    /**
     * 非阻塞延迟执行
     *
     * @param supplier  执行任务
     * @param delayTime 延迟时间
     * @param timeUnit  延迟单位
     * @return 返回 future
     */
    <T> CompletableFuture<T> delay(Supplier<T> supplier, int delayTime, TimeUnit timeUnit);

    /**
     * 非阻塞延迟执行
     *
     * @param runnable  执行任务
     * @param delayTime 延迟时间(ms)
     * @return 返回 future
     */
    default CompletableFuture<Void> delay(Runnable runnable, int delayTime) {
        return this.delay(runnable, delayTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 非阻塞延迟执行
     *
     * @param supplier  执行任务
     * @param delayTime 延迟时间(ms)
     * @return 返回 future
     */
    default <T> CompletableFuture<T> delay(Supplier<T> supplier, int delayTime) {
        return this.delay(supplier, delayTime, TimeUnit.MILLISECONDS);
    }

}