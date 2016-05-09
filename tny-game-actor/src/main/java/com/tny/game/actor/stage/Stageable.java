package com.tny.game.actor.stage;

@FunctionalInterface
public interface Stageable<TS extends TaskStage> {

    TS stage();

}
