package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.capacity.event.*;

import java.util.*;

/**
 * 可缓存能力提供器
 * Created by Kun Yang on 16/3/12.
 */
public class DefaultCapacitySupply implements InnerCapacitySupply {

	private long playerId;

	private CapacityItemModel model;

	private Item<?> item;

	public DefaultCapacitySupply(Item<?> item, CapacityItemModel model) {
		this.playerId = item.getPlayerId();
		this.item = item;
		this.model = model;
	}

	public DefaultCapacitySupply(Item<? extends CapacityItemModel> item) {
		this.playerId = item.getPlayerId();
		this.model = item.getModel();
		this.item = item;
	}

	public DefaultCapacitySupply(long playerId, CapacityItemModel model) {
		this.playerId = playerId;
		this.model = model;
	}

	@Override
	public Number getValue(Capacity capacity, Number defaultNum) {
		if (this.item != null) {
			return this.model.getAbility(this.item, defaultNum, capacity);
		} else {
			return this.model.getAbility(this.playerId, defaultNum, capacity);
		}
	}

	@Override
	public Set<CapacityGroup> getAllCapacityGroups() {
		return this.model.getCapacityGroups();
	}

	@Override
	public Number getValue(Capacity capacity) {
		if (this.item != null) {
			return this.model.getAbility(this.item, capacity, Number.class);
		} else {
			return this.model.getAbility(this.playerId, capacity, Number.class);
		}
	}

	@Override
	public Map<Capacity, Number> getAllValues() {
		if (this.item != null) {
			return this.model.getAbilitiesByType(this.item, Capacity.class, Number.class);
		} else {
			return this.model.getAbilitiesByType(this.playerId, Capacity.class, Number.class);
		}
	}

	@Override
	public boolean isHasValue(Capacity capacity) {
		return this.model.hasAbility(capacity);
	}

	@Override
	public void refresh(CapacitySupplier supplier) {
		CapacityEvents.ON_CHANGE.notify(this, supplier);
	}

	@Override
	public void invalid(CapacitySupplier supplier) {
		CapacityEvents.ON_INVALID.notify(this, supplier);
	}

	@Override
	public void effect(CapacitySupplier supplier) {
		CapacityEvents.ON_EFFECT.notify(this, supplier);
	}

}
