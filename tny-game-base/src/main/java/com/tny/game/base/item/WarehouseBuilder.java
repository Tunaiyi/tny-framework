package com.tny.game.base.item;


public abstract class WarehouseBuilder {

    /**
     * 玩家ID
     */
    protected long playerId;

    protected StorageExplorer storageExplorer;

    public WarehouseBuilder setPlayerId(long playerId) {
        this.playerId = playerId;
        return this;
    }

    public WarehouseBuilder setStorageExplorer(StorageExplorer storageExplorer) {
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
        entity.setStorageExplorer(this.storageExplorer);
        return entity;
    }

    /**
     * 获取构建Warehouse
     *
     * @return
     */
    protected abstract AbstractWarehouse createWarehouse();

}
