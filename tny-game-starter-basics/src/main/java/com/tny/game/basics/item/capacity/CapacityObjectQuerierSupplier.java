package com.tny.game.basics.item.capacity;

import com.google.common.collect.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * 组合能力提供起
 * Created by Kun Yang on 16/4/12.
 */
public interface CapacityObjectQuerierSupplier extends Capabler {

	/**
	 * @return 能力值访问器
	 */
	CapacityObjectQuerier querier();

	@Override
	default Collection<? extends CapacitySupplier> suppliers() {
		return querier().findCompositeSupplier(this.getId())
				.map(CompositeCapacitySupplier::suppliers)
				.orElse(ImmutableList.of());
	}

	@Override
	default Stream<? extends CapacitySupplier> suppliersStream() {
		return querier().findCompositeSupplier(this.getId())
				.map(CompositeCapacitySupplier::suppliersStream)
				.orElseGet(Stream::empty);
	}

	@Override
	default Set<CapacityGroup> getAllCapacityGroups() {
		return querier().findSupplier(this.getId())
				.map(CapacitySupply::getAllCapacityGroups)
				.orElse(ImmutableSet.of());
	}

}
