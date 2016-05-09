package com.tny.game.actor.stage;

import com.tny.game.actor.stage.Stages.*;
import com.tny.game.actor.stage.invok.AcceptDone;
import com.tny.game.actor.stage.invok.ApplyDone;
import com.tny.game.actor.stage.invok.ApplyStageable;
import com.tny.game.actor.stage.invok.CatcherRun;
import com.tny.game.actor.stage.invok.CatcherSupplier;
import com.tny.game.actor.stage.invok.RunDone;
import com.tny.game.actor.stage.invok.SupplyDone;
import com.tny.game.actor.stage.invok.SupplyStageable;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 内部阶段接口
 * Created by Kun Yang on 16/1/22.
 */
interface InnerTaskStage<R> extends CommonTaskStage, TypeTaskStage<R>, VoidTaskStage {

    @Override
    default boolean isDone() {
        return CommonTaskStage.super.isDone();
    }

    @Override
    default boolean isFailed() {
        return CommonTaskStage.super.isFailed();
    }

    @Override
    default boolean isSuccess() {
        return CommonTaskStage.super.isSuccess();
    }

    @Override
    default Throwable getCause() {
        return CommonTaskStage.super.getCause();
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
    default <TS extends TaskStage> TS joinBy(SupplyStageable<TS> fn) {
        checkNextExist();
        return setNext((TS) new JoinTaskStage<>(this.getHead(), new JoinSupplyStageableFragment<>(fn)), Stages.KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <TS extends TaskStage> TS joinBy(ApplyStageable<R, TS> fn) {
        checkNextExist();
        return setNext((TS) new JoinTaskStage<>(this.getHead(), new JoinApplyStageableFragment<>(fn)), Stages.KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <TS extends TaskStage> TS join(Supplier<TS> fn) {
        checkNextExist();
        return setNext((TS) new JoinTaskStage<>(this.getHead(), new JoinSupplyFragment<>(fn)), Stages.KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <TS extends TaskStage> TS join(Function<R, TS> fn) {
        checkNextExist();
        return setNext((TS) new JoinTaskStage<>(this.getHead(), new JoinApplyFragment<>(fn)), Stages.KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    default R getResult() {
        return (R) CommonTaskStage.super.getResult();
    }

    //endregion

    //region then 成功时处理
    @Override
    default VoidTaskStage thenRun(Runnable fn) {
        checkNextExist();
        return setNext(new ThenSuccessTaskStage<>(this.getHead(), new RunFragment(fn)), Stages.KEY);
    }

    @Override
    default <T> TypeTaskStage<T> thenGet(Supplier<T> fn) {
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
    default <T> TypeTaskStage<T> doneGet(SupplyDone<T> fn) {
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
    default VoidTaskStage waitUntil(BooleanSupplier fn) {
        return this.waitUntil(fn, null);
    }

    @Override
    default VoidTaskStage waitUntil(BooleanSupplier fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<Void> stage = new AwaysTaskStage(this.getHead(), new WaitRunFragment(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeTaskStage<T> waitFor(Supplier<Done<T>> fn) {
        return this.waitFor(fn, null);
    }

    @Override
    default <T> TypeTaskStage<T> waitFor(Supplier<Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new WaitSupplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TypeTaskStage<T> waitFor(Function<R, Done<T>> fn) {
        return this.waitFor(fn, null);
    }

    @Override
    default <T> TypeTaskStage<T> waitFor(Function<R, Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new WaitApplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage waitUntil(Predicate<R> fn) {
        return this.waitUntil(fn, null);
    }

    @Override
    default VoidTaskStage waitUntil(Predicate<R> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<Void> stage = new AwaysTaskStage(this.getHead(), new WaitAcceptFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }
    //endregion


}
