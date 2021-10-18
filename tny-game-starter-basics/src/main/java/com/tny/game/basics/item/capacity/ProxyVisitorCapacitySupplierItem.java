package com.tny.game.basics.item.capacity;

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class ProxyVisitorCapacitySupplierItem<IM extends CapacityItemModel> extends CapacitySupplierItem<IM>
		implements ProxyVisitorCapacitySupplier {

	protected CapacityStorer storer;

	@Override
	public CapacityVisitor visitor() {
		return storer;
	}

	protected void setStorer(CapacityStorer visitor) {
		this.storer = visitor;
	}

	@Override
	protected void refresh() {
		super.refresh();
		this.storer.saveSupplier(this.getSupplierType(), this.getId(), this.getModelId(), this.capacitySupply);
	}

	@Override
	protected void invalid() {
		super.invalid();
		this.storer.deleteSupplier(this);
	}

	@Override
	protected void effect() {
		super.effect();
		this.storer.saveSupplier(this.getSupplierType(), this.getId(), this.getModelId(), this.capacitySupply);
	}

}
