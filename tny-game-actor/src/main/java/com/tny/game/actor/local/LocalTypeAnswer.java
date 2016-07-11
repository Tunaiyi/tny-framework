package com.tny.game.actor.local;


import com.tny.game.actor.AnswerListener;
import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.stage.TypeTaskStage;
import com.tny.game.common.utils.Do;
import com.tny.game.common.utils.Done;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class LocalTypeAnswer<T> extends BaseAnswer<T, TypeTaskStage<T>> implements TypeAnswer<T> {

    private volatile List<AnswerListener<T>> listeners;

    @Override
    public void addListener(AnswerListener<T> listener) {
        if (this.listeners == null)
            this.listeners = new CopyOnWriteArrayList<>();
        this.listeners.add(listener);
    }

    @Override
    public Done<T> achieve() {
        if (!this.isDone())
            return Do.fail();
        return Do.succNullable(this.getRawValue());
    }

    // @Override
    // public TypeTaskStage<T> stage() {
    //     if (this.stage == null)
    //         this.stage = TypeAnswer.super.stage();
    //     return this.stage;
    // }

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
