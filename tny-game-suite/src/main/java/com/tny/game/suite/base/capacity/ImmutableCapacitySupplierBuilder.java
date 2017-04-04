package com.tny.game.suite.base.capacity;


import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * 不可变游戏能力值提供器构建器
 * Created by Kun Yang on 16/2/15.
 */
public class ImmutableCapacitySupplierBuilder {

    private long id;

    private int itemID;

    private long playerID;

    private DateTime endAt;

    private CapacitySupplyType type;

    private Map<Capacity, Number> capacityMap = new HashMap<>();

    private ImmutableCapacitySupplierBuilder() {
    }

    public static ImmutableCapacitySupplierBuilder newBuilder() {
        return new ImmutableCapacitySupplierBuilder();
    }

    public ImmutableCapacitySupplierBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public ImmutableCapacitySupplierBuilder setItemID(int itemID) {
        this.itemID = itemID;
        return this;
    }

    public ImmutableCapacitySupplierBuilder setPlayerID(long playerID) {
        this.playerID = playerID;
        return this;
    }

    public ImmutableCapacitySupplierBuilder setType(CapacitySupplyType type) {
        this.type = type;
        return this;
    }

    public ImmutableCapacitySupplierBuilder setEndAt(long endAt) {
        if (endAt != 0)
            this.endAt = new DateTime(endAt);
        return this;
    }

    public ImmutableCapacitySupplierBuilder setCapacityMap(Map<Capacity, Number> capacityMap) {
        this.capacityMap.putAll(capacityMap);
        return this;
    }

    public ImmutableCapacitySupplierBuilder putCapacity(Capacity capacity, Number number) {
        this.capacityMap.put(capacity, number);
        return this;
    }

    public ImmutableCapacitySupplier build() {
        return new ImmutableCapacitySupplier(
                this.id,
                itemID,
                playerID,
                type,
                endAt,
                capacityMap);
    }

}