package com.tny.game.actor.stage.invok;


import com.tny.game.actor.stage.Stageable;
import com.tny.game.actor.stage.TaskStage;

@FunctionalInterface
public interface ApplyStageable<V, TS extends TaskStage> {

    Stageable<TS> stageable(V value);

}
