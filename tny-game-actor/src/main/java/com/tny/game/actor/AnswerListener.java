package com.tny.game.actor;

public interface AnswerListener<V> {

    void onDone(Answer<V> answer, V value, Throwable reason, boolean canceled);

}
