package com.tny.game.suite.auto.snapshot;

import com.tny.game.base.item.Item;
import com.tny.game.oplog.SnapperType;

public abstract class BaseItemSnapper<I extends Item<?>, S extends ItemSnapshot> extends ItemSnapper<I, I, S> {

    protected BaseItemSnapper(SnapperType snapperType) {
        super(snapperType);
    }

    @Override
    public long getSnapshotID(I item) {
        return item.getID();
    }

    @Override
    protected I item(I object) {
        return object;
    }

}
