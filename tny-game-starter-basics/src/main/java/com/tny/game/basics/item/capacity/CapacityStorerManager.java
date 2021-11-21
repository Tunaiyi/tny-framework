package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;
import com.tny.game.data.*;

/**
 * Created by Kun Yang on 2017/7/19.
 */
public abstract class CapacityStorerManager<SC extends CapacityStorer> extends GameCacheManager<SC> {

	protected CapacityStorerManager(Class<SC> storerClass, EntityCacheManager<AnyId, SC> manager) {
		super(storerClass, manager);
	}

	public SC getStorer(long playerId) {
		return this.get(playerId);
	}

}
