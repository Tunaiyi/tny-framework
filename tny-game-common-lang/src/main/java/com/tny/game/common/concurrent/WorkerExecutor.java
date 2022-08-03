package com.tny.game.common.concurrent;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/27 20:39
 **/
public interface WorkerExecutor extends Executor {

    /**
     * 延迟执行
     *
     * @param runnable  执行任务
     * @param delayTime 延迟时间
     * @param timeUnit  延迟单位
     * @return 返回 future
     */
    CompletableFuture<Void> delay(Runnable runnable, int delayTime, TimeUnit timeUnit);

    /**
     * 延迟执行
     *
     * @param supplier  执行任务
     * @param delayTime 延迟时间
     * @param timeUnit  延迟单位
     * @return 返回 future
     */
    <T> CompletableFuture<T> delay(Supplier<T> supplier, int delayTime, TimeUnit timeUnit);

    /**
     * 延迟执行
     *
     * @param runnable  执行任务
     * @param delayTime 延迟时间(ms)
     * @return 返回 future
     */
    default CompletableFuture<Void> delay(Runnable runnable, int delayTime) {
        return this.delay(runnable, delayTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟执行
     *
     * @param supplier  执行任务
     * @param delayTime 延迟时间(ms)
     * @return 返回 future
     */
    default <T> CompletableFuture<T> delay(Supplier<T> supplier, int delayTime) {
        return this.delay(supplier, delayTime, TimeUnit.MILLISECONDS);
    }

}
