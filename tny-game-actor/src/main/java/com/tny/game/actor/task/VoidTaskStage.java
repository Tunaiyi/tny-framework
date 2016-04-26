package com.tny.game.actor.task;

import com.tny.game.actor.task.invok.CatcherRun;
import com.tny.game.actor.task.invok.RunDone;
import com.tny.game.actor.task.invok.SupplyDone;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface VoidTaskStage extends TaskStage {

    boolean isDone();

    boolean isFailed();

    boolean isSuccess();

    Throwable getCause();

    <T> TypeTaskStage<T> joinRun(Supplier<?  extends TypeTaskStage<T>> fn);

    VoidTaskStage joinSupply(Supplier<? extends VoidTaskStage> fn);

    VoidTaskStage thenRun(Runnable fn);

    <T> TypeTaskStage<T> thenSupply(Supplier<T> fn);

    VoidTaskStage doneRun(RunDone fn);

    <T> TypeTaskStage<T> doneSupply(SupplyDone<T> fn);

    VoidTaskStage thenThrow(CatcherRun catcher);

    VoidTaskStage awaitRun(BooleanSupplier fn);

    VoidTaskStage awaitRun(BooleanSupplier fn, Duration timeout);

    <T> TypeTaskStage<T> awaitSupply(Supplier<Done<T>> fn);

    <T> TypeTaskStage<T> awaitSupply(Supplier<Done<T>> fn, Duration timeout);

}
