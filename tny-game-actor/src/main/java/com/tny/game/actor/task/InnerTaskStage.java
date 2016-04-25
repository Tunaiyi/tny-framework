package com.tny.game.actor.task;

import com.tny.game.actor.task.Stages.*;
import com.tny.game.actor.task.invok.AcceptDone;
import com.tny.game.actor.task.invok.ApplyDone;
import com.tny.game.actor.task.invok.CatcherRun;
import com.tny.game.actor.task.invok.CatcherSupplier;
import com.tny.game.actor.task.invok.RunDone;
import com.tny.game.actor.task.invok.SupplyDone;
import com.tny.game.base.item.Done;
import com.tny.game.common.ExceptionUtils;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/1/22.
 */
interface InnerTaskStage<R> extends CommonTaskStage, TypeTaskStage<R> {

    @Override
    default boolean isDone() {
        return CommonTaskStage.super.isDone();
    }

    @Override
    default boolean isFailed() {
        return CommonTaskStage.super.isFailed();
    }

    default boolean isSuccess() {
        return CommonTaskStage.super.isSuccess();
    }

    @Override
    default Throwable getCause() {
        return CommonTaskStage.super.getCause();
    }

    default boolean isNoneParam() {
        return getTaskFragment().isNoneParam();
    }

    default void checkNextExist() {
        ExceptionUtils.checkState(getNext() == null, "stage next stage is exist");
    }

    //region Join 链接另一个fn返回的代码段

    @Override
    default VoidTaskStage joinSupply(Supplier<? extends VoidTaskStage> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new JoinSupplyFragment<>(fn)), Stages.KEY);
    }

    @Override
    default VoidTaskStage joinAccept(Function<R, ? extends VoidTaskStage> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new JoinApplyFragment<>(fn)), Stages.KEY);
    }

    default <T> TypeTaskStage<T> joinRun(Supplier<? extends TypeTaskStage<T>> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new JoinSupplyFragment<>(fn)), Stages.KEY);
    }

    @Override
    default <NR> TypeTaskStage<NR> joinApply(Function<R, ? extends TypeTaskStage<NR>> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new JoinApplyFragment<>(fn)), Stages.KEY);
    }

    @Override
    default Object getResult() {
        return CommonTaskStage.super.getResult();
    }

    //endregion

    //region then 成功时处理
    @Override
    default VoidTaskStage thenRun(Runnable fn) {
        checkNextExist();
        return setNext(new ThenSuccessTaskStage<>(this.getHead(), new RunFragment(fn)), Stages.KEY);
    }

    @Override
    default <T> TypeTaskStage<T> thenSupply(Supplier<T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new SupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }


    @Override
    default <N> TypeTaskStage<N> thenApply(Function<R, N> fn) {
        checkNextExist();
        InnerTaskStage<N> stage = new ThenSuccessTaskStage<>(this.getHead(), new ApplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage thenAccept(Consumer<R> fn) {
        checkNextExist();
        InnerTaskStage<Void> stage = new ThenSuccessTaskStage<>(this.getHead(), new AcceptFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    //endregion

    //region done 完成时(无论是否成功)处理
    @Override
    default VoidTaskStage doneRun(RunDone fn) {
        checkNextExist();
        InnerTaskStage<Void> stage = new ThenDoneTaskStage<>(this.getHead(), new DoneRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeTaskStage<T> doneSupply(SupplyDone<T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenDoneTaskStage<>(this.getHead(), new DoneSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeTaskStage<T> doneApply(ApplyDone<R, T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenDoneTaskStage<>(this.getHead(), new DoneApplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage doneAccept(AcceptDone<R> fn) {
        checkNextExist();
        InnerTaskStage<Void> stage = new ThenDoneTaskStage<>(this.getHead(), new DoneAcceptFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    //endregion

    //region throw 异常时处理
    @Override
    default VoidTaskStage thenThrow(CatcherRun fn) {
        checkNextExist();
        InnerTaskStage<Void> stage = new ThenDoneTaskStage<>(this.getHead(), new ThrowRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default TypeTaskStage<R> thenThrow(CatcherSupplier<R> fn) {
        checkNextExist();
        InnerTaskStage<R> stage = new ThenDoneTaskStage<>(this.getHead(), new ThrowSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    //endregion

    //region wait 等待方法返回true或返回的Done为true时继续执行
    @Override
    default VoidTaskStage awaitRun(BooleanSupplier fn) {
        return this.awaitRun(fn, null);
    }

    @Override
    default VoidTaskStage awaitRun(BooleanSupplier fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<Void> stage = new AwaysTaskStage(this.getHead(), new WaitRunFragment(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeTaskStage<T> awaitSupply(Supplier<Done<T>> fn) {
        return this.awaitSupply(fn, null);
    }

    @Override
    default <T> TypeTaskStage<T> awaitSupply(Supplier<Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new WaitSupplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeTaskStage<T> awaitApply(Function<R, Done<T>> fn) {
        return this.awaitApply(fn, null);
    }

    @Override
    default <T> TypeTaskStage<T> awaitApply(Function<R, Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new WaitApplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage awaitAccept(Predicate<R> fn) {
        return this.awaitAccept(fn, null);
    }

    @Override
    default VoidTaskStage awaitAccept(Predicate<R> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<Void> stage = new AwaysTaskStage(this.getHead(), new WaitAcceptFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }
    //endregion


}
