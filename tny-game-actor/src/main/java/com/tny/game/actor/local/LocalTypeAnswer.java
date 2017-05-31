package com.tny.game.actor.local;


import com.tny.game.actor.AnswerListener;
import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.Stage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class LocalTypeAnswer<T> extends BaseAnswer<T, Stage<T>> implements TypeAnswer<T> {

    private volatile List<AnswerListener<T>> listeners;

    @Override
    public void addListener(AnswerListener<T> listener) {
        if (this.listeners == null)
            this.listeners = new CopyOnWriteArrayList<>();
        this.listeners.add(listener);
    }

    // @Override
    // public TypeTaskStage<T> stage() {
    //     if (this.stage == null)
    //         this.stage = TypeAnswer.super.stage();
    //     return this.stage;
    // }

    @Override
    protected void fire() {
        if (this.listeners == null)
            return;
        this.listeners.forEach(l -> {
            try {
                l.done(this, getRawValue(), getRawCause(), isCancelled());
            } catch (Throwable e) {
                LOGGER.error("{}.done 异常", l.getClass(), e);
            }
        });
    }

}
