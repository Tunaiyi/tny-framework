package com.tny.game.actor.stage;

import com.tny.game.actor.*;
import com.tny.game.common.result.*;

import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class Flows {

    public static Done<Throwable> getCause(Stage stage) {
        if (!stage.isDone()) {
            return DoneResults.failure();
        }
        return DoneResults.successNullable(stage.getCause());
    }

    public static Completable time(Duration duration) {
        return new TimeAwait(duration)::get;
    }

    public static <T> DoneSupplier<T> time(T object, Duration duration) {
        return new TimeAwaitWith<>(object, duration)::get;
    }

    public static VoidFlow of() {
        return new LinkedFlow<>();
    }

    public static VoidFlow of(String name) {
        return new LinkedFlow<>(name);
    }

    public static VoidFlow of(Executor executor) {
        return new LinkedFlow<>().switchTo(executor);
    }

    public static VoidFlow of(String name, Executor executor) {
        return new LinkedFlow<>(name).switchTo(executor);
    }

    public static VoidFlow of(Runnable fn) {
        return new LinkedFlow<>().thenRun(fn);
    }

    public static VoidFlow of(String name, Runnable fn) {
        return new LinkedFlow<>(name).thenRun(fn);
    }

    public static <T> TypeFlow<T> of(Supplier<T> fn) {
        return new LinkedFlow<>().thenGet(fn);
    }

    public static <T> TypeFlow<T> of(String name, Supplier<T> fn) {
        return new LinkedFlow<>(name).thenGet(fn);
    }

    //region wait 等待方法返回true或返回的Done为true时继续执行
    public static VoidFlow waitUntil(Iterable<? extends Completable> fns) {
        return waitUntil(fns, null);
    }

    public static VoidFlow waitUntil(Iterable<? extends Completable> fns, Duration timeout) {
        return new LinkedFlow<>().waitUntil(() -> {
            for (Completable fn : fns)
                if (!fn.isCompleted()) {
                    return false;
                }
            return true;
        }, timeout);
    }

    public static VoidFlow waitUntil(Completable fn) {
        return waitUntil(fn, null);
    }

    public static VoidFlow waitUntil(Completable fn, Duration timeout) {
        return new LinkedFlow<>().waitUntil(fn, timeout);
    }

    public static VoidFlow waitTime(Duration duration) {
        return waitUntil(new TimeAwait(duration)::get, null);
    }

    public static <T> TypeFlow<T> waitTime(T object, Duration duration) {
        return waitFor(new TimeAwaitWith<>(object, duration)::get, null);
    }

    public static <T> TypeFlow<T> waitFor(DoneSupplier<T> fn) {
        return waitFor(fn, null);
    }

    public static <T> TypeFlow<T> waitFor(DoneSupplier<T> fn, Duration timeout) {
        return new LinkedFlow<>().waitFor(fn, timeout);
    }

    public static <T> TypeFlow<List<T>> waitFor(Iterable<? extends Supplier<Done<T>>> fns) {
        return waitFor(fns, null);
    }

    public static <T> TypeFlow<List<T>> waitFor(Iterable<? extends Supplier<Done<T>>> fns, Duration timeout) {
        return new LinkedFlow<>().waitFor(() -> {
            List<T> result = new ArrayList<>();
            for (Supplier<Done<T>> fn : fns) {
                Done<T> done = fn.get();
                if (!done.isSuccess()) {
                    return DoneResults.failure();
                } else {
                    result.add(done.get());
                }
            }
            return DoneResults.success(result);
        }, timeout);
    }

    public static <K, T> TypeFlow<Map<K, T>> waitFor(Map<K, ? extends Supplier<Done<T>>> fns) {
        return waitFor(fns, null);
    }

    public static <K, T> TypeFlow<Map<K, T>> waitFor(Map<K, ? extends Supplier<Done<T>>> fns, Duration timeout) {
        return new LinkedFlow<>().waitFor(() -> {
            for (Supplier<Done<T>> fn : fns.values()) {
                Done<T> done = fn.get();
                if (!done.isSuccess()) {
                    return DoneResults.failure();
                }
            }
            return DoneResults.success(fns.entrySet().stream()
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            e -> e.getValue().get().get()
                    )));
        }, timeout);
    }

    public static <T> TypeFlow<T> waitFor(Future<T> future) {
        return waitFor(future, null);
    }

    public static <T> TypeFlow<T> waitFor(Future<T> future, Duration duration) {
        return waitFor(new FutureAwait<>(future), duration);
    }
    //endregion

    //    public static <R, T> TaskStage<T> awaitApply(Function<R, Done<T>> fn) {
    //        return awaitApply(fn, null);
    //    }
    //
    //    public static <R, T> TaskStage<T> awaitApply(Function<R, Done<T>> fn, Duration timeout) {
    //        return new ThenSuccessTaskStage<>(null, new WaitApplyFragment<>(fn, timeout));
    //    }
    //
    //    public static <R, T> TaskStage<T> awaitApply(TaskFunction<R, Done<T>> fn) {
    //        return awaitApply(fn, null);
    //    }
    //
    //    public static <R, T> TaskStage<T> awaitApply(TaskFunction<R, Done<T>> fn, Duration timeout) {
    //        return new ThenSuccessTaskStage<>(null, new TaskWaitApplyFragment<>(fn, timeout));
    //    }
    //
    //    public static <R> VoidTaskStage awaitAccept(Predicate<R> fn) {
    //        return awaitAccept(fn, null);
    //    }
    //
    //    public static <R> VoidTaskStage awaitAccept(Predicate<R> fn, Duration timeout) {
    //        return new AwaysVoidTaskStage(null, new WaitAcceptFragment<>(fn, timeout));
    //    }
    //
    //    public static <R> VoidTaskStage awaitAccept(TaskPredicate<R> fn) {
    //        return awaitAccept(fn, null);
    //    }
    //
    //    public static <R> VoidTaskStage awaitAccept(TaskPredicate<R> fn, Duration timeout) {
    //        return new AwaysVoidTaskStage(null, new TaskWaitAcceptFragment<>(fn, timeout));
    //    }

}
