package com.tny.game.suite.auto.snapshot;

import com.tny.game.base.item.Item;
import com.tny.game.oplog.SnapperType;

public abstract class ProxyItemSnapper<O, I extends Item<?>, S extends ItemSnapshot> extends ProxySnapper<O, I, S> {

    protected ProxyItemSnapper(SnapperType snapperType) {
        super(snapperType);
    }

    @Override
    public long getSnapshotID(O object) {
        return other(object).getID();
    }

}
