package com.tny.game.actor.invoke;

@FunctionalInterface
public interface A1Caller<R, A1> extends ActorSender {

    R call(A1 arg1);

}
