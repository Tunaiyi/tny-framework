package com.tny.game.actor;

@FunctionalInterface
public interface CallAvailable<V, R> {

    Available<R> apply(V t);

}
