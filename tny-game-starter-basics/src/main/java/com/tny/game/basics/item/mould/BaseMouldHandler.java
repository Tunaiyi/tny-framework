package com.tny.game.basics.item.mould;

import com.tny.game.basics.mould.*;

public abstract class BaseMouldHandler<M extends Mould, C> implements MouldHandler {

    private final M mould;

    public BaseMouldHandler(M mould) {
        super();
        this.mould = mould;
    }

    @Override
    public Mould getMould() {
        return this.mould;
    }

    public abstract void loadContext(FeatureLauncher launcher, C context);

    @Override
    public void removeMould(FeatureLauncher launcher) {
    }

    @Override
    public String toString() {
        return "GameMould [getMould()=" + this.getMould() + "]";
    }

}
