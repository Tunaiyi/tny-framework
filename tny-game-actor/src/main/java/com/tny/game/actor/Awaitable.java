package com.tny.game.actor;


public interface Awaitable<V> {

	boolean isDone();

	V result();

}