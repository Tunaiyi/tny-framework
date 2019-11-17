package com.tny.game.suite.base.capacity;

import com.tny.game.suite.base.*;

/**
 * Created by Kun Yang on 2017/7/19.
 */
public abstract class CapacityStorerManager<SC extends CapacityStorer> extends GameCacheManager<SC> {

    protected CapacityStorerManager(Class<SC> storerClass) {
        super(storerClass);
    }

    public SC getStorer(long playerId) {
        return this.get(playerId);
    }

}
