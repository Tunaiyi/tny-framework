package com.tny.game.basics.item.capacity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.tny.game.basics.item.*;

import java.util.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByCopyCapacitySupplier extends BaseStoreCapable implements StoreCapacitySupplier {

	private long id;

	private int itemID;

	private long playerId;

	private CapacitySupplierType type;

	private ImmutableMap<Capacity, Number> capacityMap;

	private Set<CapacityGroup> groups;

	StoreByCopyCapacitySupplier(CapacitySupplierType type, long id, int itemID, long playerId, Map<Capacity, Number> capacityMap,
			Set<CapacityGroup> groups, long expireAt) {
		super(expireAt);
		this.id = id;
		this.itemID = itemID;
		this.playerId = playerId;
		this.type = type;
		this.capacityMap = ImmutableMap.copyOf(capacityMap);
		this.groups = Collections.unmodifiableSet(groups);
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public int getItemId() {
		return this.itemID;
	}

	@Override
	public long getPlayerId() {
		return this.playerId;
	}

	@Override
	public CapacitySupplierType getSupplierType() {
		return this.type;
	}

	@Override
	public boolean isHasValue(Capacity capacity) {
		if (!this.isSupplying()) {
			return false;
		}
		return getAllValues().containsKey(capacity);
	}

	@Override
	public Number getValue(Capacity capacity, Number defaultValue) {
		if (!this.isSupplying()) {
			return defaultValue;
		}
		Number number = getAllValues().get(capacity);
		return number == null ? defaultValue : number;
	}

	@Override
	public Set<CapacityGroup> getAllCapacityGroups() {
		return this.groups;
	}

	@Override
	public Number getValue(Capacity capacity) {
		return getValue(capacity, null);
	}

	@Override
	public Map<Capacity, Number> getAllValues() {
		if (!this.isSupplying()) {
			return ImmutableMap.of();
		}
		return this.capacityMap;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", this.id)
				.add("itemId", this.itemID)
				.add("name", ItemModels.name(this.itemID))
				.toString();
	}

}