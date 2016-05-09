package com.tny.game.actor.invoke;

@FunctionalInterface
public interface A0Caller<R> extends ActorSender {

    R call();

}
