package com.tny.game.actor.invoke;

@FunctionalInterface
public interface A2Runner<A1, A2> extends ActorSender {

    void run(A1 arg1, A2 arg2);

}
