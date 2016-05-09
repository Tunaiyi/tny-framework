package com.tny.game.actor.invoke;

@FunctionalInterface
public interface A5Caller<R, A1, A2, A3, A4, A5> extends ActorSender {

    R call(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

}
