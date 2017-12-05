package com.tny.game.common.utils;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameWarehouseManager {

    GameWarehouse getWarehouse(long playerID);

    void saveWarehouse(GameWarehouse warehouse);

}
