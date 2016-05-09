package com.tny.game.actor.stage;


import com.tny.game.actor.stage.invok.CatcherRun;
import com.tny.game.actor.stage.invok.RunDone;
import com.tny.game.actor.stage.invok.SupplyDone;
import com.tny.game.actor.stage.invok.SupplyStageable;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface VoidTaskStage extends TaskStage {

    <TS extends TaskStage> TS joinBy(SupplyStageable<TS> fn);

    <TS extends TaskStage> TS join(Supplier<TS> fn);

    VoidTaskStage thenRun(Runnable fn);

    <T> TypeTaskStage<T> thenGet(Supplier<T> fn);

    VoidTaskStage doneRun(RunDone fn);

    <T> TypeTaskStage<T> doneGet(SupplyDone<T> fn);

    VoidTaskStage thenThrow(CatcherRun catcher);

    VoidTaskStage waitUntil(BooleanSupplier fn);

    VoidTaskStage waitUntil(BooleanSupplier fn, Duration timeout);

    <T> TypeTaskStage<T> waitFor(Supplier<Done<T>> fn);

    <T> TypeTaskStage<T> waitFor(Supplier<Done<T>> fn, Duration timeout);

}
