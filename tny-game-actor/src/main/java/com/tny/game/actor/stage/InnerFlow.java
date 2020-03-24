package com.tny.game.actor.stage;

import com.tny.game.actor.*;
import com.tny.game.actor.stage.Stages.*;
import com.tny.game.actor.stage.invok.*;
import com.tny.game.common.result.*;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.*;

/**
 * Created by Kun Yang on 2017/5/31.
 */
interface InnerFlow<R> extends VoidFlow, TypeFlow<R> {

    @Override
    default R getResult() {
        return TypeFlow.super.getResult();
    }

    <F extends Flow> F add(InnerStage<?> stage);

    Fragment<?, ?> getTaskFragment();

    @Override
    InnerFlow<R> switchTo(Executor executor);

    @Override
    InnerFlow<R> start();

    @Override
    InnerFlow<R> start(Executor executor);

    //region Join 链接另一个fn返回的代码段

    @Override
    @SuppressWarnings("unchecked")
    default <T> TypeFlow<T> joinFor(Object name, Supplier<DoneSupplier<T>> fn) {
        return add(new JoinStage<>(name, new JoinSupplierDoneSupplierFragment<>(fn)));
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T> TypeFlow<T> joinFor(Object name, Function<R, DoneSupplier<T>> fn) {
        return add(new JoinStage<>(name, new JoinApplyDoneSupplierFragment<>(fn)));
    }

    @Override
    default VoidFlow joinUntil(Object name, Supplier<Completable> fn) {
        return add(new JoinStage<>(name, new JoinSupplierCompletableFragment(fn)));
    }

    @Override
    default VoidFlow joinUntil(Object name, Function<R, Completable> fn) {
        return add(new JoinStage<>(name, new JoinApplyCompletableFragment<>(fn)));
    }

    // @Override
    // @SuppressWarnings("unchecked")
    // default <T> TypeFlow<T> join(Object name, Supplier<Stage<T>> fn) {
    //     return add(new JoinStage<>(name, new JoinSupplyFragment<>(fn)));
    // }
    //
    // @Override
    // @SuppressWarnings("unchecked")
    // default <TS extends Stage> TS join(Object name, Function<R, TS> fn) {
    //     return add((TS) new JoinStage<>(name, new JoinApplyFragment<>(fn)));
    // }
    //
    // @Override
    // @SuppressWarnings("unchecked")
    // default Done<R> getResult(Object name, ) {
    //     return (R) CommonStage.super.getResult();
    // }

    //endregion

    //region then 成功时处理
    @Override
    default VoidFlow thenRun(Object name, Runnable fn) {
        return add(new ThenSuccessStage<>(name, new RunFragment(fn)));
    }

    @Override
    default <T> TypeFlow<T> thenGet(Object name, Supplier<T> fn) {
        return add(new ThenSuccessStage<>(name, new SupplyFragment<>(fn)));
    }


    @Override
    default <N> TypeFlow<N> thenApply(Object name, Function<R, N> fn) {
        return add(new ThenSuccessStage<>(name, new ApplyFragment<>(fn)));
    }

    @Override
    default VoidFlow thenAccept(Object name, Consumer<R> fn) {
        return add(new ThenSuccessStage<>(name, new AcceptFragment<>(fn)));
    }

    //endregion

    //region done 完成时(无论是否成功)处理
    @Override
    default VoidFlow doneRun(Object name, RunDone fn) {
        return add(new ThenDoneStage<>(name, new DoneRunFragment(fn)));
    }

    @Override
    default <T> TypeFlow<T> doneGet(Object name, SupplyDone<T> fn) {
        return add(new ThenDoneStage<>(name, new DoneSupplyFragment<>(fn)));
    }

    @Override
    default <T> TypeFlow<T> doneApply(Object name, ApplyDone<R, T> fn) {
        return add(new ThenDoneStage<>(name, new DoneApplyFragment<>(fn)));
    }

    @Override
    default VoidFlow doneAccept(Object name, AcceptDone<R> fn) {
        return add(new ThenDoneStage<>(name, new DoneAcceptFragment<>(fn)));
    }

    //endregion

    //region throw 异常时处理
    @Override
    default VoidFlow thenThrow(Object name, CatcherRun fn) {
        return add(new ThenDoneStage<>(name, new ThrowRunFragment(fn)));
    }

    @Override
    default TypeFlow<R> thenThrow(Object name, CatcherSupplier<R> fn) {
        return add(new ThenDoneStage<>(name, new ThrowSupplyFragment<>(fn)));
    }

    //endregion

    //region wait 等待方法返回true或返回的Done为true时继续执行
    @Override
    default VoidFlow waitUntil(Object name, Completable fn) {
        return this.waitUntil(name, fn, null);
    }

    @Override
    default VoidFlow waitUntil(Object name, Completable fn, Duration timeout) {
        return add(new AwaysStage(name, new WaitRunFragment(fn, timeout)));
    }

    @Override
    default <T> TypeFlow<T> waitFor(Object name, DoneSupplier<T> fn) {
        return this.waitFor(name, fn, null);
    }

    @Override
    default <T> TypeFlow<T> waitFor(Object name, DoneSupplier<T> fn, Duration timeout) {
        return add(new ThenSuccessStage<>(name, new WaitSupplyFragment<>(fn, timeout)));
    }

    @Override
    default <T> TypeFlow<T> waitFor(Object name, Function<R, Done<T>> fn) {
        return this.waitFor(name, fn, null);
    }

    @Override
    default <T> TypeFlow<T> waitFor(Object name, Function<R, Done<T>> fn, Duration timeout) {
        return add(new ThenSuccessStage<>(name, new WaitApplyFragment<>(fn, timeout)));
    }

    @Override
    default VoidFlow waitUntil(Object name, Predicate<R> fn) {
        return this.waitUntil(name, fn, null);
    }

    @Override
    default VoidFlow waitUntil(Object name, Predicate<R> fn, Duration timeout) {
        return add(new AwaysStage(name, new WaitAcceptFragment<>(fn, timeout)));
    }
    //endregion

}
