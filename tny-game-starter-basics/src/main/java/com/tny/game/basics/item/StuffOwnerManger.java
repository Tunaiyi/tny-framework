package com.tny.game.basics.item;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/25 4:33 下午
 */
public interface StuffOwnerManger<O extends StuffOwner<?, ?>> extends Manager<O>, ItemTypesManager {

	ItemType getOwnerItemType();

	default Set<ItemType> manageTypes() {
		return ImmutableSet.of(getOwnerItemType());
	}

}
