package com.tny.game.basics.item;

public abstract class WarehouseBuilder {

	/**
	 * 玩家ID
	 */
	private long playerId;

	private StuffOwnerExplorer storageExplorer;

	public WarehouseBuilder setPlayerId(long playerId) {
		this.playerId = playerId;
		return this;
	}

	public WarehouseBuilder setStorageExplorer(StuffOwnerExplorer storageExplorer) {
		this.storageExplorer = storageExplorer;
		return this;
	}

	/**
	 * 构建storage对象
	 *
	 * @return Storage对象
	 */
	public Warehouse build() {
		AbstractWarehouse entity = this.createWarehouse();
		entity.setPlayerId(this.playerId);
		entity.setStuffOwnerExplorer(this.storageExplorer);
		return entity;
	}

	/**
	 * 获取构建Warehouse
	 *
	 * @return
	 */
	protected abstract AbstractWarehouse createWarehouse();

}
