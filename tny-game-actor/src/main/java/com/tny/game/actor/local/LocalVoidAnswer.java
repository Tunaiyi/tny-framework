package com.tny.game.actor.local;


import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.VoidAnswerListener;
import com.tny.game.actor.stage.VoidStage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocalVoidAnswer extends BaseAnswer<Void, VoidStage> implements VoidAnswer {

    private volatile List<VoidAnswerListener> listeners;

    @Override
    public void addListener(VoidAnswerListener listener) {
        if (this.listeners == null)
            this.listeners = new CopyOnWriteArrayList<>();
        this.listeners.add(listener);
    }

    // @Override
    // public VoidTaskStage stage() {
    //     if (this.stage == null)
    //         this.stage = VoidAnswer.super.stage();
    //     return this.stage;
    // }

    @Override
    protected void fire() {
        if (this.listeners == null)
            return;
        this.listeners.forEach(l -> {
            try {
                l.done(this, getRawCause(), isCancelled());
            } catch (Throwable e) {
                LOGGER.error("{}.done 异常", l.getClass(), e);
            }
        });
    }

}
