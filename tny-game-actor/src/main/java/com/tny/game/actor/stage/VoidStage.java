package com.tny.game.actor.stage;


import com.tny.game.actor.Completable;
import com.tny.game.actor.DoneSupplier;
import com.tny.game.actor.stage.invok.CatcherRun;
import com.tny.game.actor.stage.invok.RunDone;
import com.tny.game.actor.stage.invok.SupplyDone;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 无类型阶段
 * Created by Kun Yang on 16/1/23.
 */
public interface VoidStage extends Stage {

    VoidStage joinUntil(Supplier<Completable> fn);

    <T> TypeStage<T> joinFor(Supplier<DoneSupplier<T>> fn);

    <TS extends Stage> TS join(Supplier<TS> fn);

    VoidStage thenRun(Runnable fn);

    <T> TypeStage<T> thenGet(Supplier<T> fn);

    VoidStage doneRun(RunDone fn);

    <T> TypeStage<T> doneGet(SupplyDone<T> fn);

    VoidStage thenThrow(CatcherRun catcher);

    VoidStage waitUntil(Completable fn);

    VoidStage waitUntil(Completable fn, Duration timeout);

    <T> TypeStage<T> waitFor(DoneSupplier<T> fn);

    <T> TypeStage<T> waitFor(DoneSupplier<T> fn, Duration timeout);

}
