package com.tny.game.actor.invoke;

@FunctionalInterface
public interface A2Caller<R, A1, A2> extends ActorSender {

    R call(A1 arg1, A2 arg2);

}
