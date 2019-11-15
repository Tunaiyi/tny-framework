package com.tny.game.actor.stage;

import com.tny.game.actor.*;
import com.tny.game.actor.stage.invok.*;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.*;

/**
 * 流程
 * Created by Kun Yang on 2017/5/30.
 */
public interface VoidFlow extends Flow {

    @Override
    default Object getResult() {
        return null;
    }

    default VoidFlow joinUntil(Supplier<Completable> fn) {
        return this.joinUntil(null, fn);
    }

    VoidFlow joinUntil(Object name, Supplier<Completable> fn);

    default <T> TypeFlow<T> joinFor(Supplier<DoneSupplier<T>> fn) {
        return this.joinFor(null, fn);
    }

    <T> TypeFlow<T> joinFor(Object name, Supplier<DoneSupplier<T>> fn);

    // default <TS extends Stage> TS join(Supplier<TS> fn) {
    //     return this.
    // join(null,fn);
    // }

    // <TS extends Stage> TS join(Object name, Supplier<TS> fn);

    default VoidFlow thenRun(Runnable fn) {
        return this.thenRun(null, fn);
    }

    VoidFlow thenRun(Object name, Runnable fn);

    default <T> TypeFlow<T> thenGet(Supplier<T> fn) {
        return this.thenGet(null, fn);
    }

    <T> TypeFlow<T> thenGet(Object name, Supplier<T> fn);

    default VoidFlow doneRun(RunDone fn) {
        return this.doneRun(null, fn);
    }

    VoidFlow doneRun(Object name, RunDone fn);

    default <T> TypeFlow<T> doneGet(SupplyDone<T> fn) {
        return this.doneGet(null, fn);
    }

    <T> TypeFlow<T> doneGet(Object name, SupplyDone<T> fn);

    default VoidFlow thenThrow(CatcherRun catcher) {
        return this.thenThrow(null, catcher);
    }

    VoidFlow thenThrow(Object name, CatcherRun catcher);

    default VoidFlow waitUntil(Completable fn) {
        return this.waitUntil(null, fn);

    }

    default VoidFlow waitUntil(Object name, Completable fn) {
        return this.waitUntil(null, fn);

    }

    default VoidFlow waitUntil(Completable fn, Duration timeout) {
        return this.waitUntil(null, fn, timeout);
    }

    VoidFlow waitUntil(Object name, Completable fn, Duration timeout);

    default <T> TypeFlow<T> waitFor(DoneSupplier<T> fn) {
        return this.waitFor(null, fn);

    }

    default <T> TypeFlow<T> waitFor(Object name, DoneSupplier<T> fn) {
        return this.waitFor(null, fn, null);

    }

    default <T> TypeFlow<T> waitFor(DoneSupplier<T> fn, Duration timeout) {
        return this.waitFor(null, fn, timeout);
    }

    <T> TypeFlow<T> waitFor(Object name, DoneSupplier<T> fn, Duration timeout);

    VoidFlow switchTo(Executor executor);

    @Override
    VoidFlow start();

    @Override
    VoidFlow start(Executor executor);

    default VoidFlow start(Runnable onSuccess) {
        return this.start(null, onSuccess, null, null);
    }

    default VoidFlow start(Runnable onSuccess, Consumer<Throwable> onError) {
        return this.start(onSuccess, onError, null);
    }

    default VoidFlow start(Runnable onSuccess, Consumer<Throwable> onError, Consumer<Throwable> onFinish) {
        return this.start(null, onSuccess, onError, onFinish);
    }

    default VoidFlow start(Executor executor, Runnable onSuccess) {
        return this.start(executor, onSuccess);
    }

    default VoidFlow start(Executor executor, Runnable onSuccess, Consumer<Throwable> onError) {
        return this.start(executor, onSuccess, onError, null);
    }

    VoidFlow start(Executor executor, Runnable onSuccess, Consumer<Throwable> onError, Consumer<Throwable> onFinish);

}
