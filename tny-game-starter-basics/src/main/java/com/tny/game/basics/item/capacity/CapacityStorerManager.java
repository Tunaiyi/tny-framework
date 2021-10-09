package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;

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
