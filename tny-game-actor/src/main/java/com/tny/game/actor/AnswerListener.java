package com.tny.game.actor;

public interface AnswerListener<V> {

	void done(Answer<V> answer, V value, Throwable reason, boolean canceled);

}
