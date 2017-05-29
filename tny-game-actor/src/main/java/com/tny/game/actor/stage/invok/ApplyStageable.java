package com.tny.game.actor.stage.invok;


import com.tny.game.actor.stage.Stage;
import com.tny.game.actor.stage.Stageable;

@FunctionalInterface
public interface ApplyStageable<V, TS extends Stage> {

    Stageable<TS> stageable(V value);

}
