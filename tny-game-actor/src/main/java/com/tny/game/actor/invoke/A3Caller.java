package com.tny.game.actor.invoke;

@FunctionalInterface
public interface A3Caller<R, A1, A2, A3> extends ActorSender {

    R call(A1 arg1, A2 arg2, A3 arg3);

}
