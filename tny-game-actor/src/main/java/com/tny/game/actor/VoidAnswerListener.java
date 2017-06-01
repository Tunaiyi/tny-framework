package com.tny.game.actor;

public interface VoidAnswerListener extends AnswerListener<Void> {

    @Override
    default void onDone(Answer<Void> answer, Void value, Throwable reason, boolean canceled) {
        this.doOnDone(answer, reason, canceled);
    }

    void doOnDone(Answer<Void> answer, Throwable reason, boolean canceled);

}
