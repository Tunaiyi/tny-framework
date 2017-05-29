package com.tny.game.actor.stage;

import com.tny.game.actor.Completable;
import com.tny.game.actor.DoneSupplier;
import com.tny.game.actor.stage.Stages.*;
import com.tny.game.actor.stage.invok.AcceptDone;
import com.tny.game.actor.stage.invok.ApplyDone;
import com.tny.game.actor.stage.invok.CatcherRun;
import com.tny.game.actor.stage.invok.CatcherSupplier;
import com.tny.game.actor.stage.invok.RunDone;
import com.tny.game.actor.stage.invok.SupplyDone;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 内部阶段接口
 * Created by Kun Yang on 16/1/22.
 */
interface InnerStage<R> extends CommonStage, TypeStage<R>, VoidStage {

    @Override
    default boolean isDone() {
        return CommonStage.super.isDone();
    }

    @Override
    default boolean isFailed() {
        return CommonStage.super.isFailed();
    }

    @Override
    default boolean isSuccess() {
        return CommonStage.super.isSuccess();
    }

    @Override
    default Throwable getCause() {
        return CommonStage.super.getCause();
    }

    @Override
    default boolean isNoneParam() {
        return getTaskFragment().isNoneParam();
    }

    default void checkNextExist() {
        ExceptionUtils.checkState(getNext() == null, "stage next stage is exist");
    }

    //region Join 链接另一个fn返回的代码段

    @Override
    @SuppressWarnings("unchecked")
    default <T> TypeStage<T> joinFor(Supplier<DoneSupplier<T>> fn) {
        checkNextExist();
        return setNext((TypeStage<T>) new JoinStage<>(this.getHead(), new JoinSupplierAvailableFragment<>(fn)), Stages.KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T> TypeStage<T> joinFor(Function<R, DoneSupplier<T>> fn) {
        checkNextExist();
        return setNext((TypeStage<T>) new JoinStage<>(this.getHead(), new JoinApplyAvailableFragment<>(fn)), Stages.KEY);
    }

    @Override
    default VoidStage joinUntil(Supplier<Completable> fn) {
        checkNextExist();
        return setNext(new JoinStage<>(this.getHead(), new JoinSupplierCompletableFragment(fn)), Stages.KEY);
    }

    @Override
    default VoidStage joinUntil(Function<R, Completable> fn) {
        checkNextExist();
        return setNext(new JoinStage<>(this.getHead(), new JoinApplyCompletableFragment<>(fn)), Stages.KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <TS extends Stage> TS join(Supplier<TS> fn) {
        checkNextExist();
        return setNext((TS) new JoinStage<>(this.getHead(), new JoinSupplyFragment<>(fn)), Stages.KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <TS extends Stage> TS join(Function<R, TS> fn) {
        checkNextExist();
        return setNext((TS) new JoinStage<>(this.getHead(), new JoinApplyFragment<>(fn)), Stages.KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    default R getResult() {
        return (R) CommonStage.super.getResult();
    }

    //endregion

    //region then 成功时处理
    @Override
    default VoidStage thenRun(Runnable fn) {
        checkNextExist();
        return setNext(new ThenSuccessStage<>(this.getHead(), new RunFragment(fn)), Stages.KEY);
    }

    @Override
    default <T> TypeStage<T> thenGet(Supplier<T> fn) {
        checkNextExist();
        InnerStage<T> stage = new ThenSuccessStage<>(this.getHead(), new SupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }


    @Override
    default <N> TypeStage<N> thenApply(Function<R, N> fn) {
        checkNextExist();
        InnerStage<N> stage = new ThenSuccessStage<>(this.getHead(), new ApplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidStage thenAccept(Consumer<R> fn) {
        checkNextExist();
        InnerStage<Void> stage = new ThenSuccessStage<>(this.getHead(), new AcceptFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    //endregion

    //region done 完成时(无论是否成功)处理
    @Override
    default VoidStage doneRun(RunDone fn) {
        checkNextExist();
        InnerStage<Void> stage = new ThenDoneStage<>(this.getHead(), new DoneRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeStage<T> doneGet(SupplyDone<T> fn) {
        checkNextExist();
        InnerStage<T> stage = new ThenDoneStage<>(this.getHead(), new DoneSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeStage<T> doneApply(ApplyDone<R, T> fn) {
        checkNextExist();
        InnerStage<T> stage = new ThenDoneStage<>(this.getHead(), new DoneApplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidStage doneAccept(AcceptDone<R> fn) {
        checkNextExist();
        InnerStage<Void> stage = new ThenDoneStage<>(this.getHead(), new DoneAcceptFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    //endregion

    //region throw 异常时处理
    @Override
    default VoidStage thenThrow(CatcherRun fn) {
        checkNextExist();
        InnerStage<Void> stage = new ThenDoneStage<>(this.getHead(), new ThrowRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default TypeStage<R> thenThrow(CatcherSupplier<R> fn) {
        checkNextExist();
        InnerStage<R> stage = new ThenDoneStage<>(this.getHead(), new ThrowSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    //endregion

    //region wait 等待方法返回true或返回的Done为true时继续执行
    @Override
    default VoidStage waitUntil(Completable fn) {
        return this.waitUntil(fn, null);
    }

    @Override
    default VoidStage waitUntil(Completable fn, Duration timeout) {
        checkNextExist();
        InnerStage<Void> stage = new AwaysStage(this.getHead(), new WaitRunFragment(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeStage<T> waitFor(DoneSupplier<T> fn) {
        return this.waitFor(fn, null);
    }

    @Override
    default <T> TypeStage<T> waitFor(DoneSupplier<T> fn, Duration timeout) {
        checkNextExist();
        InnerStage<T> stage = new ThenSuccessStage<>(this.getHead(), new WaitSupplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeStage<T> waitFor(Function<R, Done<T>> fn) {
        return this.waitFor(fn, null);
    }

    @Override
    default <T> TypeStage<T> waitFor(Function<R, Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerStage<T> stage = new ThenSuccessStage<>(this.getHead(), new WaitApplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidStage waitUntil(Predicate<R> fn) {
        return this.waitUntil(fn, null);
    }

    @Override
    default VoidStage waitUntil(Predicate<R> fn, Duration timeout) {
        checkNextExist();
        InnerStage<Void> stage = new AwaysStage(this.getHead(), new WaitAcceptFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }
    //endregion


}
