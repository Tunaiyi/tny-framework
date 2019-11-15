package com.tny.game.suite.auto.snapshot;

import com.tny.game.oplog.*;

public abstract class ProxySnapper<O, I, S extends Snapshot> extends BaseSnapper<O, S> {

    protected ProxySnapper(SnapperType snapperType) {
        super(snapperType);
    }

    @Override
    public S toSnapshot(O object) {
        I other = other(object);
        Snapper<I, S> snapper = snapper();
        S snapshot = snapper.toSnapshot(other);
        this.setSnapshot(snapshot, object);
        return snapshot;
    }

    @Override
    public void update(S snapshot, O object) {
        I other = other(object);
        Snapper<I, S> snapper = snapper();
        snapper.update(snapshot, other);
        this.updateSnapshot(snapshot, object);
    }

    public abstract void updateSnapshot(S snapshot, O object);

    public abstract Snapper<I, S> snapper();

    public abstract I other(O object);

}
