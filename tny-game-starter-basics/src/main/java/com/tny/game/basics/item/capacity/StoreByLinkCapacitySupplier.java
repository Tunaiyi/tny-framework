package com.tny.game.basics.item.capacity;

import com.google.common.base.MoreObjects;
import com.tny.game.basics.item.*;

import java.util.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByLinkCapacitySupplier extends BaseStoreCapable implements StoreCapacitySupplier {

	private CapacitySupplier supplier;

	StoreByLinkCapacitySupplier(CapacitySupplier supplier, long expireAt) {
		super(expireAt);
		this.supplier = supplier;
	}

	@Override
	public long getId() {
		return supplier.getId();
	}

	public int getModelId() {
		return supplier.getModelId();
	}

	@Override
	public long getPlayerId() {
		return supplier.getPlayerId();
	}

	@Override
	public CapacitySupplierType getSupplierType() {
		return supplier.getSupplierType();
	}

	@Override
	public boolean isHasValue(Capacity capacity) {
		return supplier.isHasValue(capacity);
	}

	@Override
	public Number getValue(Capacity capacity, Number defaultValue) {
		return supplier.getValue(capacity, defaultValue);
	}

	@Override
	public Set<CapacityGroup> getAllCapacityGroups() {
		return supplier.getAllCapacityGroups();
	}

	@Override
	public Number getValue(Capacity capacity) {
		return getValue(capacity, null);
	}

	@Override
	public Map<Capacity, Number> getAllValues() {
		return supplier.getAllValues();
	}

	@Override
	public boolean isLinked() {
		return true;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", supplier.getId())
				.add("modelId", supplier.getModelId())
				.add("name", ItemModels.name(supplier.getModelId()))
				.toString();
	}

}