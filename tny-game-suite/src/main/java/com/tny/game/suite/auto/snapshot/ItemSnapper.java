package com.tny.game.suite.auto.snapshot;

import com.tny.game.basics.item.*;
import com.tny.game.oplog.*;

public abstract class ItemSnapper<O, I extends Item<?>, S extends ItemSnapshot> extends BaseSnapper<O, S> {

    protected ItemSnapper(SnapperType snapperType) {
        super(snapperType);
    }

    @Override
    public S toSnapshot(O object) {
        I item = item(object);
        if (item == null) {
            return null;
        }
        S snapshot = super.toSnapshot(object);
        if (snapshot == null) {
            return null;
        }
        snapshot.setIDs(item.getItemId(), item.getId());
        snapshot.setPid(item.getPlayerId());
        this.setSnapshot(snapshot, object);
        return snapshot;
    }

    @Override
    public long getSnapshotId(O object) {
        return item(object).getId();
    }

    protected abstract I item(O object);

}