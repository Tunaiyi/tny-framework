package com.tny.game.suite.base.capacity;


import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.tny.game.base.item.ItemModels;

import java.util.Map;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByCopyCapacitySupplier extends BaseStoreCapacitiable implements StoreCapacitySupplier {

    private long id;

    private int itemID;

    private long playerID;

    private CapacitySupplierType type;

    private ImmutableMap<Capacity, Number> capacityMap;

    StoreByCopyCapacitySupplier(CapacitySupplierType type, long id, int itemID, long playerID, Map<Capacity, Number> capacityMap, long expireAt) {
        super(expireAt);
        this.id = id;
        this.itemID = itemID;
        this.playerID = playerID;
        this.type = type;
        this.capacityMap = ImmutableMap.copyOf(capacityMap);
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public int getItemID() {
        return itemID;
    }

    @Override
    public long getPlayerID() {
        return playerID;
    }

    @Override
    public CapacitySupplierType getSupplierType() {
        return type;
    }

    @Override
    public boolean isHasValue(Capacity capacity) {
        if (!this.isSupplying())
            return false;
        return getAllValues().containsKey(capacity);
    }

    @Override
    public Number getValue(Capacity capacity, Number defaultValue) {
        if (!this.isSupplying())
            return defaultValue;
        Number number = getAllValues().get(capacity);
        return number == null ? defaultValue : number;
    }


    @Override
    public Number getValue(Capacity capacity) {
        return getValue(capacity, null);
    }


    @Override
    public Map<Capacity, Number> getAllValues() {
        if (!this.isSupplying())
            return ImmutableMap.of();
        return capacityMap;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("itemID", itemID)
                .add("name", ItemModels.name(itemID))
                .toString();
    }

}