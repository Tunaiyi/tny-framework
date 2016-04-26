package com.tny.game.actor.task;

import com.tny.game.actor.task.invok.AcceptDone;
import com.tny.game.actor.task.invok.ApplyDone;
import com.tny.game.actor.task.invok.CatcherSupplier;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Kun Yang on 16/1/22.
 */
public interface TypeTaskStage<R> extends VoidTaskStage {

    Object getResult();

    <NR> TypeTaskStage<NR> joinApply(Function<R, ? extends TypeTaskStage<NR>> fn);

    VoidTaskStage joinAccept(Function<R, ? extends VoidTaskStage> fn);

    <N> TypeTaskStage<N> thenApply(Function<R, N> fn);

    VoidTaskStage thenAccept(Consumer<R> fn);

    <T> TypeTaskStage<T> doneApply(ApplyDone<R, T> fn);

    VoidTaskStage doneAccept(AcceptDone<R> fn);

    TypeTaskStage<R> thenThrow(CatcherSupplier<R> fn);

    <T> TypeTaskStage<T> awaitApply(Function<R, Done<T>> fn);

    <T> TypeTaskStage<T> awaitApply(Function<R, Done<T>> fn, Duration timeout);

    VoidTaskStage awaitAccept(Predicate<R> fn);

    VoidTaskStage awaitAccept(Predicate<R> fn, Duration timeout);

}
