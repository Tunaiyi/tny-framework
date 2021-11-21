package com.tny.game.basics.item;

import java.util.*;

/**
 * Storage构建器
 *
 * @param <S>
 * @param <O>
 * @param <B>
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public abstract class StuffOwnerBuilder<IM extends ItemModel, S extends Stuff<?>, O extends StuffOwner<IM, S>, B
		extends StuffOwnerBuilder<IM, S, O, B>> {

	/**
	 * 玩家ID
	 */
	protected long playerId;

	/**
	 * 物品模型列表
	 */
	protected Set<S> itemSet = new HashSet<>();

	/**
	 * 设置玩家id
	 *
	 * @param playerId 玩家id
	 * @return 返回storageBuilder对象
	 */
	public B setPlayerId(long playerId) {
		this.playerId = playerId;
		return (B)this;
	}

	/**
	 * 添加item对象
	 *
	 * @param item 添加的item对象
	 * @return 返回storageBuilder对象
	 */
	public B addItem(S item) {
		this.itemSet.add(item);
		return (B)this;
	}

	/**
	 * 添加item对象列表
	 *
	 * @param itemList 添加的item对象列表
	 * @return 返回storageBuilder对象
	 */
	public B addItem(Set<S> itemList) {
		for (S item : itemList)
			this.itemSet.add(item);
		return (B)this;
	}

	/**
	 * 构建storage对象
	 *
	 * @return Storage对象
	 */
	public O build() {
		AbstractStuffOwner<IM, ?, S> entity = createItemStorage();
		entity.setPlayerId(this.playerId);
		for (S item : this.itemSet) {
			entity.itemMap.put(item.getId(), item);
		}
		return (O)entity;
	}

	/**
	 * 获取构建storage
	 *
	 * @return
	 */
	protected abstract AbstractStuffOwner<IM, ?, S> createItemStorage();

}
