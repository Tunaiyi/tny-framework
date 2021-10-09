package com.tny.game.basics.item;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author KGTny
 * @ClassName: owner
 * @Description: 物品项存储器
 * @date 2011-11-3 上午9:50:52
 * <p>
 * <p>
 * <br>
 */
public interface StuffOwner<M extends ItemModel, S extends Stuff<?>> extends Item<M> {

	/**
	 * 获取某事物的信息 <br>
	 *
	 * @param id 物品ID
	 * @return 物品信息
	 */
	S getItemById(long id);

	/**
	 * 获取某事物的信息 <br>
	 *
	 * @param itemID 物品ID
	 * @return 物品信息
	 */
	default List<S> getItemsByItemId(int itemID) {
		S stuff = getItemByItemId(itemID);
		if (stuff != null) {
			return ImmutableList.of(stuff);
		}
		return ImmutableList.of();
	}

	/**
	 * 获取某事物的信息 <br>
	 *
	 * @param itemID 物品ID
	 * @return 物品信息
	 */
	S getItemByItemId(int itemID);

}