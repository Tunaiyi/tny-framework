package com.tny.game.basics.item.capacity;

import java.util.Collection;

public interface CapacitySupplierComposition extends CapableComposition {

	/**
	 * 接受指定的 supplier
	 *
	 * @param supplier 指定的 supplier
	 */
	default boolean accept(CapacitySupplier supplier) {
		return this.doAccept(supplier);
	}

	default boolean accept(Collection<? extends CapacitySupplier> suppliers) {
		boolean acc = false;
		for (CapacitySupplier supplier : suppliers) {
			if (this.doAccept(supplier)) {
				acc = true;
			}
		}
		return acc;
	}

	/**
	 * 移除指定的 supplier
	 *
	 * @param supplier 指定的 supplier
	 */
	default boolean remove(CapacitySupplier supplier) {
		return this.doRemove(supplier);
	}

	default boolean remove(Collection<? extends CapacitySupplier> suppliers) {
		return suppliers.stream().anyMatch(this::doRemove);
	}

	boolean doAccept(CapacitySupplier supplier);

	boolean doRemove(CapacitySupplier supplier);

	void clear();

}
