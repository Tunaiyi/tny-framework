package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

import java.util.Set;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/20 1:11 上午
 */
public interface StuffService<SM extends ItemModel> {

	/**
	 * @return 处理商品类型
	 */
	Set<ItemType> getDealStuffTypes();

	/**
	 * 奖励时候是否溢出
	 *
	 * @param warehouse  仓库
	 * @param stuffModel 添加的物品模型
	 * @param check      检测模式
	 * @param number     数量
	 * @return 如果溢出返回 ture, 否则返回 false
	 */
	default boolean isOverflow(Warehouse warehouse, SM stuffModel, AlterType check, Number number) {
		return false;
	}

	/**
	 * 扣除时候是否足够
	 *
	 * @param warehouse  仓库
	 * @param stuffModel 添加的物品模型
	 * @param check      检测模式
	 * @param number     数量
	 * @return 如果不够返回 ture, 否则返回 false
	 */
	default boolean isNotEnough(Warehouse warehouse, SM stuffModel, AlterType check, Number number) {
		return false;
	}

	/**
	 * 扣除物品
	 *
	 * @param warehouse  仓库
	 * @param tradeItem  扣除项
	 * @param action     扣除的原因
	 * @param attributes 参数
	 */
	void deduct(Warehouse warehouse, TradeItem<SM> tradeItem, Action action, Attributes attributes);

	/**
	 * 奖励物品
	 *
	 * @param warehouse  仓库
	 * @param tradeItem  奖励项
	 * @param action     扣除的原因
	 * @param attributes 参数
	 */
	void reward(Warehouse warehouse, TradeItem<SM> tradeItem, Action action, Attributes attributes);

}
