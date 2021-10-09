package com.tny.game.suite.base;

import com.tny.game.basics.item.*;

import javax.annotation.Resource;

/**
 * 存储当前存储的Item存储在一个Storage上的Manager
 *
 * @author KGTny
 */
public abstract class GameSaveByStorageManager<S extends Stuff<?>, O extends StuffOwner<?, S>> extends GameSaveByOtherManager<S, O> {

	@Resource
	private GameWarehouseManager gameWarehouseManager;

	private ItemType ownItemType;

	protected GameSaveByStorageManager(Class<? extends S> entityClass, ItemType ownItemType) {
		super(entityClass);
		this.ownItemType = ownItemType;
	}

	@Override
	protected O getSaveObject(S item) {
		return this.getSaveObject(item.getPlayerId());
	}

	protected O getSaveObject(long playerId) {
		GameWarehouse holder = this.gameWarehouseManager.getWarehouse(playerId);
		if (holder == null) {
			return null;
		}
		return (O)holder.getOwner(this.ownItemType);
	}

	@Override
	protected S get(long playerId, Object... object) {
		O storage = this.getSaveObject(playerId);
		return storage.getItemById(((Number)object[0]).longValue());
	}

	@Override
	protected abstract Manager<O> getManager(S item);

	@Override
	protected S getInstance(long playerId, Object... object) {
		O storage = this.getSaveObject(playerId);
		return storage.getItemById((long)object[0]);
	}

}
