package com.tny.game.basics.item.capacity;

import com.google.common.collect.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public interface CapacityObjectQuerierCapabler extends Capabler {

	CapacityObjectQuerier querier();

	@Override
	default Collection<? extends CapacitySupplier> suppliers() {
		return querier().findCapabler(this.getId())
				.map(CapacitySettler::suppliers)
				.orElse(ImmutableList.of());
	}

	@Override
	default Stream<? extends CapacitySupplier> suppliersStream() {
		return querier().findCapabler(this.getId())
				.map(CapacitySettler::suppliersStream)
				.orElse(Stream.empty());
	}

	@Override
	default Set<CapacityGroup> getAllCapacityGroups() {
		return querier().findCapabler(this.getId())
				.map(Capabler::getAllCapacityGroups)
				.orElse(ImmutableSet.of());
	}

}
