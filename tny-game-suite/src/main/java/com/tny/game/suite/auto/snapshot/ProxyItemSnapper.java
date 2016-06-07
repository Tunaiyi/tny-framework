package com.tny.game.suite.auto.snapshot;

import com.tny.game.base.item.Item;
import com.tny.game.oplog.Snapper;
import com.tny.game.oplog.SnapperType;
import com.tny.game.oplog.SnapshotType;

public abstract class ProxyItemSnapper<I extends Item<?>, O extends Item<?>, S extends ItemSnapshot> implements Snapper<I, S> {

    private SnapperType snapperType;

    protected ProxyItemSnapper(SnapperType snapperType) {
        this.snapperType = snapperType;
    }

    @Override
    public S toSnapshot(I item) {
        O other = other(item);
        Snapper<O, S> snapper = snapper();
        S snapshot = snapper.toSnapshot(other);
        this.setSnapshot(snapshot, item);
        return snapshot;
    }

    @Override
    public void update(S snapshot, I item) {
        O other = other(item);
        Snapper<O, S> snapper = snapper();
        snapper.update(snapshot, other);
        this.updateSnapshot(snapshot, item);
    }

    @Override
    public long getSnapshotID(I item) {
        return other(item).getID();
    }

    @Override
    public SnapshotType getSnapshotType() {
        return this.snapperType.getSnapshotType();
    }

    @Override
    public SnapperType getSnapperType() {
        return this.snapperType;
    }

    public abstract void setSnapshot(S snapshot, I item);

    public abstract void updateSnapshot(S snapshot, I item);

    public abstract Snapper<O, S> snapper();

    public abstract O other(I item);

}
