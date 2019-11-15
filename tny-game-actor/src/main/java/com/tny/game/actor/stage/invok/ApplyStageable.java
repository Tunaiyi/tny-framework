package com.tny.game.actor.stage.invok;


import com.tny.game.actor.stage.*;

@FunctionalInterface
public interface ApplyStageable<V, TS extends Stage> {

    Stageable<TS> stageable(V value);

}
