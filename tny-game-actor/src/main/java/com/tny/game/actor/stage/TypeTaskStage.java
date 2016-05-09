package com.tny.game.actor.stage;


import com.tny.game.actor.stage.invok.AcceptDone;
import com.tny.game.actor.stage.invok.ApplyDone;
import com.tny.game.actor.stage.invok.ApplyStageable;
import com.tny.game.actor.stage.invok.CatcherSupplier;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Kun Yang on 16/1/22.
 */
public interface TypeTaskStage<R> extends TaskStage {

    R getResult();

    <TS extends TaskStage> TS joinBy(ApplyStageable<R, TS> fn);

    <TS extends TaskStage> TS join(Function<R, TS> fn);

    VoidTaskStage thenAccept(Consumer<R> fn);

    <N> TypeTaskStage<N> thenApply(Function<R, N> fn);

    VoidTaskStage doneAccept(AcceptDone<R> fn);

    <T> TypeTaskStage<T> doneApply(ApplyDone<R, T> fn);

    TypeTaskStage<R> thenThrow(CatcherSupplier<R> fn);

    <T> TypeTaskStage<T> waitFor(Function<R, Done<T>> fn);

    <T> TypeTaskStage<T> waitFor(Function<R, Done<T>> fn, Duration timeout);

    VoidTaskStage waitUntil(Predicate<R> fn);

    VoidTaskStage waitUntil(Predicate<R> fn, Duration timeout);

}
