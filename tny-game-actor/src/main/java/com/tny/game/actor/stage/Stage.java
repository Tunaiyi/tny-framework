package com.tny.game.actor.stage;


/**
 * 有类型阶段
 * Created by Kun Yang on 16/1/22.
 */
public interface Stage<R> {

    boolean isDone();

    boolean isFailed();

    boolean isSuccess();

    Throwable getCause();

    R getResult();

    /**
     * 将fn返回的Completable加入Stage,并等待Completable完成
     *
     * @param fn 业务方法
     * @return 返回无类型Stage
     */
    // VoidStage joinUntil(Function<R, Completable> fn);
    //
    // VoidStage joinUntil(Supplier<Completable> fn);
    //
    // <T> Stage<T> joinFor(Function<R, DoneSupplier<T>> fn);
    //
    // <T> Stage<T> joinFor(Supplier<DoneSupplier<T>> fn);
    //
    // <TS extends javafx.stage.Stage> TS join(Function<R, TS> fn);
    //
    // <TS extends javafx.stage.Stage> TS join(Supplier<TS> fn);
    //
    // VoidStage thenAccept(Consumer<R> fn);
    //
    // VoidStage thenRun(Runnable fn);
    //
    // <N> Stage<N> thenApply(Function<R, N> fn);
    //
    // <T> Stage<T> thenGet(Supplier<T> fn);
    //
    // VoidStage doneAccept(AcceptDone<R> fn);
    //
    // VoidStage doneRun(RunDone fn);
    //
    // <T> Stage<T> doneApply(ApplyDone<R, T> fn);
    //
    // <T> Stage<T> doneGet(SupplyDone<T> fn);
    //
    // Stage<R> thenThrow(CatcherSupplier<R> fn);
    //
    // VoidStage thenThrow(CatcherRun catcher);
    //
    // <T> Stage<T> waitFor(Function<R, Done<T>> fn);
    //
    // <T> Stage<T> waitFor(Function<R, Done<T>> fn, Duration timeout);
    //
    // <T> Stage<T> waitFor(DoneSupplier<T> fn);
    //
    // <T> Stage<T> waitFor(DoneSupplier<T> fn, Duration timeout);
    //
    // VoidStage waitUntil(Predicate<R> fn);
    //
    // VoidStage waitUntil(Predicate<R> fn, Duration timeout);
    //
    // VoidStage waitUntil(Completable fn);
    //
    // VoidStage waitUntil(Completable fn, Duration timeout);

}
