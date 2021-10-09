package com.tny.game.suite.auto.snapshot;

import com.tny.game.basics.item.*;
import com.tny.game.oplog.*;

public abstract class ProxyItemSnapper<O, I extends Item<?>, S extends ItemSnapshot> extends ProxySnapper<O, I, S> {

    protected ProxyItemSnapper(SnapperType snapperType) {
        super(snapperType);
    }

    @Override
    public long getSnapshotId(O object) {
        return other(object).getId();
    }

}
