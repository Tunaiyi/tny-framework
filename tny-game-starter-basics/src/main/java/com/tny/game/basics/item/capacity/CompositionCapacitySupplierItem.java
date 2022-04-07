package com.tny.game.basics.item.capacity;

import java.util.Collection;

public abstract class CompositionCapacitySupplierItem<IM extends CapacitySupplierItemModel>
		extends CapacitySupplierItem<IM> implements CompositionCapacitySupplier {

	private final transient CapacitySupplierComposition composition;

	protected CompositionCapacitySupplierItem() {
		this(new DefaultCapacitySupplierComposition());
	}

	protected CompositionCapacitySupplierItem(CapacitySupplierComposition composition) {
		this.composition = composition;
	}

	@Override
	public CapacitySupplierComposition composition() {
		return composition;
	}

	protected void accept(CapacitySupplier supplier) {
		this.composition.accept(supplier);
	}

	protected void accept(Collection<? extends CapacitySupplier> suppliers) {
		this.composition.accept(suppliers);
	}

	protected void remove(CapacitySupplier supplier) {
		this.composition.remove(supplier);
	}

	protected void remove(Collection<CapacitySupplier> suppliers) {
		this.composition.remove(suppliers);
	}

	protected void clear() {
		this.composition.clear();
	}

	@Override
	protected void refresh() {

	}

	@Override
	protected void invalid() {

	}

	@Override
	protected void effect() {

	}

}
