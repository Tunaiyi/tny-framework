package com.tny.game.basics.item;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameWarehouseManager {

    GameWarehouse getWarehouse(long playerId);

    void saveWarehouse(GameWarehouse warehouse);

}