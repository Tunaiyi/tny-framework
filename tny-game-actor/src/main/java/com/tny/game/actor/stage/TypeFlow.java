package com.tny.game.actor.stage;

import com.tny.game.actor.Completable;
import com.tny.game.actor.DoneSupplier;
import com.tny.game.actor.stage.invok.AcceptDone;
import com.tny.game.actor.stage.invok.ApplyDone;
import com.tny.game.actor.stage.invok.CatcherSupplier;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 流程
 * Created by Kun Yang on 2017/5/30.
 */
public interface TypeFlow<V> extends Flow {

    Done<V> getDone();

    @Override
    default V getResult() {
        return getDone().get();
    }

    /**
     * 将fn返回的Completable加入Stage,并等待Completable完成
     *
     * @param fn 业务方法
     * @return 返回无类型Stage
     */
    default VoidFlow joinUntil(Function<V, Completable> fn) {
        return this.joinUntil(null, fn);
    }

    VoidFlow joinUntil(Object name, Function<V, Completable> fn);

    default <T> TypeFlow<T> joinFor(Function<V, DoneSupplier<T>> fn) {
        return this.joinFor(null, fn);
    }

    <T> TypeFlow<T> joinFor(Object name, Function<V, DoneSupplier<T>> fn);

    // default <TS extends Stage> TS join(Function<V, TS> fn) {
    //     return this.join(null, fn);
    // }

    // <TS extends Stage> TS join(Object name, Function<V, TS> fn);

    default VoidFlow thenAccept(Consumer<V> fn) {
        return this.thenAccept(null, fn);
    }

    VoidFlow thenAccept(Object name, Consumer<V> fn);

    default <N> TypeFlow<N> thenApply(Function<V, N> fn) {
        return this.thenApply(null, fn);
    }

    <N> TypeFlow<N> thenApply(Object name, Function<V, N> fn);

    default VoidFlow doneAccept(AcceptDone<V> fn) {
        return this.doneAccept(null, fn);
    }

    VoidFlow doneAccept(Object name, AcceptDone<V> fn);

    default <T> TypeFlow<T> doneApply(ApplyDone<V, T> fn) {
        return this.doneApply(null, fn);
    }

    <T> TypeFlow<T> doneApply(Object name, ApplyDone<V, T> fn);

    default TypeFlow<V> thenThrow(CatcherSupplier<V> fn) {
        return this.thenThrow(null, fn);
    }

    TypeFlow<V> thenThrow(Object name, CatcherSupplier<V> fn);

    default <T> TypeFlow<T> waitFor(Function<V, Done<T>> fn) {
        return this.waitFor(null, fn);
    }

    <T> TypeFlow<T> waitFor(Object name, Function<V, Done<T>> fn);

    default <T> TypeFlow<T> waitFor(Function<V, Done<T>> fn, Duration timeout) {
        return this.waitFor(null, fn, timeout);
    }

    <T> TypeFlow<T> waitFor(Object name, Function<V, Done<T>> fn, Duration timeout);

    default VoidFlow waitUntil(Predicate<V> fn) {
        return this.waitUntil(null, fn);
    }

    VoidFlow waitUntil(Object name, Predicate<V> fn);

    default VoidFlow waitUntil(Predicate<V> fn, Duration timeout) {
        return this.waitUntil(null, fn, timeout);
    }

    VoidFlow waitUntil(Object name, Predicate<V> fn, Duration timeout);

    TypeFlow<V> switchTo(Executor executor);

    @Override
    TypeFlow<V> start();

    @Override
    TypeFlow<V> start(Executor executor);

    default TypeFlow<V> start(Consumer<V> onSuccess) {
        return this.start(onSuccess, null, null);
    }

    default TypeFlow<V> start(Consumer<V> onSuccess, Consumer<Throwable> onError) {
        return this.start(onSuccess, onError, null);
    }

    default TypeFlow<V> start(Consumer<V> onSuccess, Consumer<Throwable> onError, BiConsumer<V, Throwable> onFinish) {
        return this.start(null, onSuccess, onError, null);
    }

    default TypeFlow<V> start(Executor executor, Consumer<V> onSuccess) {
        return this.start(executor, onSuccess, null, null);
    }

    default TypeFlow<V> start(Executor executor, Consumer<V> onSuccess, Consumer<Throwable> onError) {
        return this.start(executor, onSuccess, onError, null);
    }

    TypeFlow<V> start(Executor executor, Consumer<V> onSuccess, Consumer<Throwable> onError, BiConsumer<V, Throwable> onFinish);

}
