package com.tny.game.basics.item;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/20 1:11 上午
 */
public interface PrimaryStuffService<SM extends ItemModel> extends StuffService<SM> {

	@Override
	default Set<ItemType> getDealStuffTypes() {
		return Collections.emptySet();
	}

}
