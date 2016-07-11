package com.tny.game.actor.stage;


import com.tny.game.actor.Available;
import com.tny.game.actor.BeFinished;
import com.tny.game.actor.SupplyAvailable;
import com.tny.game.actor.SupplyBeFinished;
import com.tny.game.actor.stage.invok.CatcherRun;
import com.tny.game.actor.stage.invok.RunDone;
import com.tny.game.actor.stage.invok.SupplyDone;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface VoidTaskStage extends TaskStage {

    VoidTaskStage joinRun(SupplyBeFinished fn);

    <T> TypeTaskStage<T> joinGet(SupplyAvailable<T> fn);

    <TS extends TaskStage> TS join(Supplier<TS> fn);

    VoidTaskStage thenRun(Runnable fn);

    <T> TypeTaskStage<T> thenGet(Supplier<T> fn);

    VoidTaskStage doneRun(RunDone fn);

    <T> TypeTaskStage<T> doneGet(SupplyDone<T> fn);

    VoidTaskStage thenThrow(CatcherRun catcher);

    VoidTaskStage waitUntil(BeFinished fn);

    VoidTaskStage waitUntil(BeFinished fn, Duration timeout);

    <T> TypeTaskStage<T> waitFor(Available<T> fn);

    <T> TypeTaskStage<T> waitFor(Available<T> fn, Duration timeout);

}
