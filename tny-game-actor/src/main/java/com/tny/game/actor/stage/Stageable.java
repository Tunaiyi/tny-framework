package com.tny.game.actor.stage;

@FunctionalInterface
public interface Stageable<TS extends Stage> {

    TS stage();

}
