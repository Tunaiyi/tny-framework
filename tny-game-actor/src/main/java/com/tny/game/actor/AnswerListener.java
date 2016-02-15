package com.tny.game.actor;

public interface AnswerListener<V> {

	void answerWith(Answer<V> answer, V value, Throwable reason);

}
