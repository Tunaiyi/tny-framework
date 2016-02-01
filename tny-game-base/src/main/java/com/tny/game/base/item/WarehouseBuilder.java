package com.tny.game.base.item;


public abstract class WarehouseBuilder {

    /**
     * 玩家ID
     */
    protected long playerID;

    protected OwnerExplorer ownerExplorer;

    public WarehouseBuilder setPlayerID(long playerID) {
        this.playerID = playerID;
        return this;
    }

    public WarehouseBuilder setOwnerExplorer(OwnerExplorer ownerExplorer) {
        this.ownerExplorer = ownerExplorer;
        return this;
    }

    /**
     * 构建owner对象
     *
     * @return owner对象
     */
    public Warehouse build() {
        AbstractWarehouse entity = this.createWarehouse();
        entity.setPlayerID(this.playerID);
        entity.setOwnerExplorer(this.ownerExplorer);
        return entity;
    }

    /**
     * 获取构建Warehouse
     *
     * @return
     */
    protected abstract AbstractWarehouse createWarehouse();

}
