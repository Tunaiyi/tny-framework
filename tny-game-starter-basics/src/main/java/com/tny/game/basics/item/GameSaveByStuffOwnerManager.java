package com.tny.game.basics.item;

import javax.annotation.Resource;

/**
 * 存储当前存储的Item存储在一个Storage上的Manager
 *
 * @author KGTny
 */
public abstract class GameSaveByStuffOwnerManager<S extends Stuff<?>, O extends GameStuffOwner<?, ?, S>> extends GameSaveByOtherManager<S, O> {

	@Resource
	private GameWarehouseManager gameWarehouseManager;

	private final ItemType ownItemType;

	protected GameSaveByStuffOwnerManager(Class<? extends S> entityClass, ItemType ownItemType) {
		super(entityClass);
		this.ownItemType = ownItemType;
	}

	@Override
	protected O getSaveObject(S item) {
		return this.getSaveObject(item.getPlayerId());
	}

	protected O getSaveObject(long playerId) {
		GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerId);
		if (warehouse == null) {
			return null;
		}
		return warehouse.getOwner(this.ownItemType);
	}

	@Override
	protected S get(long playerId, long id) {
		O owner = this.getSaveObject(playerId);
		return owner.getItemById(id);
	}

	@Override
	protected abstract Manager<O> getManager(S item);

	@Override
	protected S getInstance(long playerId, Object... object) {
		O owner = this.getSaveObject(playerId);
		return owner.getItemById((long)object[0]);
	}

}
