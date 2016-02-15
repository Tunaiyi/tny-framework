package com.tny.game.actor.local;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 代理ExecutorService
 * Created by Kun Yang on 16/1/20.
 */
public interface ExecutorServiceDelegate extends ExecutorService {

    ExecutorService executorService();

    default boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService().awaitTermination(timeout, unit);
    }

    default <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executorService().invokeAll(tasks);
    }

    default <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return executorService().invokeAll(tasks, timeout, unit);
    }

    default <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return executorService().invokeAny(tasks);
    }

    default <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return executorService().invokeAny(tasks, timeout, unit);
    }

    default boolean isShutdown() {
        return executorService().isShutdown();
    }

    default boolean isTerminated() {
        return executorService().isTerminated();
    }

    default void shutdown() {
        executorService().shutdown();
    }

    default List<Runnable> shutdownNow() {
        return executorService().shutdownNow();
    }

    default <T> Future<T> submit(Callable<T> task) {
        return executorService().submit(task);
    }

    default Future<?> submit(Runnable task) {
        return executorService().submit(task);
    }

    default <T> Future<T> submit(Runnable task, T result) {
        return executorService().submit(task, result);
    }

    default void execute(Runnable command) {
        executorService().execute(command);
    }


}
