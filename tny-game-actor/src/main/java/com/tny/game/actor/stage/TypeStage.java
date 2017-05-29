package com.tny.game.actor.stage;


import com.tny.game.actor.Completable;
import com.tny.game.actor.DoneSupplier;
import com.tny.game.actor.stage.invok.AcceptDone;
import com.tny.game.actor.stage.invok.ApplyDone;
import com.tny.game.actor.stage.invok.CatcherRun;
import com.tny.game.actor.stage.invok.CatcherSupplier;
import com.tny.game.actor.stage.invok.RunDone;
import com.tny.game.actor.stage.invok.SupplyDone;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 有类型阶段
 * Created by Kun Yang on 16/1/22.
 */
public interface TypeStage<R> extends Stage {

    R getResult();

    /**
     * 将fn返回的Completable加入Stage,并等待Completable完成
     *
     * @param fn 业务方法
     * @return 返回无类型Stage
     */
    VoidStage joinUntil(Function<R, Completable> fn);

    VoidStage joinUntil(Supplier<Completable> fn);

    <T> TypeStage<T> joinFor(Function<R, DoneSupplier<T>> fn);

    <T> TypeStage<T> joinFor(Supplier<DoneSupplier<T>> fn);

    <TS extends Stage> TS join(Function<R, TS> fn);

    <TS extends Stage> TS join(Supplier<TS> fn);

    VoidStage thenAccept(Consumer<R> fn);

    VoidStage thenRun(Runnable fn);

    <N> TypeStage<N> thenApply(Function<R, N> fn);

    <T> TypeStage<T> thenGet(Supplier<T> fn);

    VoidStage doneAccept(AcceptDone<R> fn);

    VoidStage doneRun(RunDone fn);

    <T> TypeStage<T> doneApply(ApplyDone<R, T> fn);

    <T> TypeStage<T> doneGet(SupplyDone<T> fn);

    TypeStage<R> thenThrow(CatcherSupplier<R> fn);

    VoidStage thenThrow(CatcherRun catcher);

    <T> TypeStage<T> waitFor(Function<R, Done<T>> fn);

    <T> TypeStage<T> waitFor(Function<R, Done<T>> fn, Duration timeout);

    <T> TypeStage<T> waitFor(DoneSupplier<T> fn);

    <T> TypeStage<T> waitFor(DoneSupplier<T> fn, Duration timeout);

    VoidStage waitUntil(Predicate<R> fn);

    VoidStage waitUntil(Predicate<R> fn, Duration timeout);

    VoidStage waitUntil(Completable fn);

    VoidStage waitUntil(Completable fn, Duration timeout);

}
