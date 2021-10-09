package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;

/**
 * 能力值相关对象
 */
public interface CapacityObject extends AnyOwned {

	/**
	 * @return 获取能力提供者ID
	 */
	int getItemId();

}