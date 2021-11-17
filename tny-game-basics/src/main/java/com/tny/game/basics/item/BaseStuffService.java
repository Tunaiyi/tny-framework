package com.tny.game.basics.item;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/17 4:16 下午
 */
public abstract class BaseStuffService<SM extends ItemModel> implements StuffService<SM> {

	private final Set<ItemType> stuffItemTypes;

	protected BaseStuffService(Set<ItemType> stuffItemTypes) {
		this.stuffItemTypes = ImmutableSet.copyOf(stuffItemTypes);
	}

	protected BaseStuffService(ItemType... stuffItemTypes) {
		this.stuffItemTypes = ImmutableSet.copyOf(stuffItemTypes);
	}

	@Override
	public Set<ItemType> getDealStuffTypes() {
		return stuffItemTypes;
	}

}
