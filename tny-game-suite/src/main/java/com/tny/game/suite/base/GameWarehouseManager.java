package com.tny.game.suite.base;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameWarehouseManager {

    GameWarehouse getWarehouse(long playerId);

    void saveWarehouse(GameWarehouse warehouse);

}
