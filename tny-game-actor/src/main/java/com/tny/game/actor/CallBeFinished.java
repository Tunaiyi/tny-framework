package com.tny.game.actor;

@FunctionalInterface
public interface CallBeFinished<T> {

    BeFinished apply(T t);

}
