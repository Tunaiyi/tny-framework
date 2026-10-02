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
package com.tny.game.common.concurrent.worker;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/2/24 12:29
 **/
public interface SerialAsyncWorker extends AsyncWorker {

    /**
     * 阻塞等待 action 的 future 完成
     *
     * @param action 执行任务
     * @return 返回 future
     */
    <T> CompletableFuture<T> await(AsyncAction<T> action);

    /**
     * 阻塞等待 action 的 future 完成
     *
     * @param action 执行任务
     * @return 返回 future
     */
    <T> CompletableFuture<T> await(AsyncAction<T> action, long timeout);

    /**
     * 阻塞等待 action 的 future 完成
     *
     * @param action 执行任务
     * @return 返回 future
     */
    <T> CompletableFuture<T> await(AsyncAction<T> action, long timeout, TimeUnit unit);

    /**
     * Worker阻塞延迟执行
     *
     * @param delayTime 延迟时间
     * @return 返回 future
     */
    default CompletableFuture<Void> awaitDelay(int delayTime) {
        return awaitDelay(delayTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Worker阻塞延迟执行
     *
     * @param delayTime 延迟时间
     * @param timeUnit  延迟单位
     * @return 返回 future
     */
    default CompletableFuture<Void> awaitDelay(int delayTime, TimeUnit timeUnit) {
        return awaitDelay(NOOP, delayTime, timeUnit);
    }

    /**
     * Worker阻塞延迟执行
     *
     * @param runnable  执行任务
     * @param delayTime 延迟时间
     * @param timeUnit  延迟单位
     * @return 返回 future
     */
    CompletableFuture<Void> awaitDelay(Runnable runnable, int delayTime, TimeUnit timeUnit);

    /**
     * Worker阻塞延迟执行
     *
     * @param supplier  执行任务
     * @param delayTime 延迟时间
     * @param timeUnit  延迟单位
     * @return 返回 future
     */
    <T> CompletableFuture<T> awaitDelay(Supplier<T> supplier, int delayTime, TimeUnit timeUnit);

    /**
     * Worker阻塞延迟执行
     *
     * @param runnable  执行任务
     * @param delayTime 延迟时间(ms)
     * @return 返回 future
     */
    default CompletableFuture<Void> awaitDelay(Runnable runnable, int delayTime) {
        return this.awaitDelay(runnable, delayTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Worker阻塞延迟执行
     *
     * @param supplier  执行任务
     * @param delayTime 延迟时间(ms)
     * @return 返回 future
     */
    default <T> CompletableFuture<T> awaitDelay(Supplier<T> supplier, int delayTime) {
        return this.awaitDelay(supplier, delayTime, TimeUnit.MILLISECONDS);
    }

}
