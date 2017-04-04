package com.tny.game.suite.base.capacity;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.Set;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class ImmutableCapacitySupplier implements TimeoutCapacitySupplier {

    private long id;

    private int itemID;

    private long playerID;

    private CapacitySupplyType type;

    private ImmutableMap<Capacity, Number> capacityMap;

    private DateTime endAt = null;

    protected ImmutableCapacitySupplier(CapacitySupplier supplier) {
        this.id = supplier.getID();
        this.itemID = supplier.getItemID();
        this.playerID = supplier.getPlayerID();
        this.type = supplier.getSupplyType();
        this.capacityMap = ImmutableMap.copyOf(supplier.getAllCapacityValue());
        if (supplier instanceof TimeoutCapacitySupplier) {
            TimeoutCapacitySupplier timeoutSupplier = (TimeoutCapacitySupplier) supplier;
            this.endAt = timeoutSupplier.getEndAt();
        }
    }

    protected ImmutableCapacitySupplier(long id, int itemID, long playerID, CapacitySupplyType type, DateTime endAt, Map<Capacity, Number> capacityMap) {
        this.id = id;
        this.itemID = itemID;
        this.playerID = playerID;
        this.type = type;
        this.capacityMap = ImmutableMap.copyOf(capacityMap);
        this.endAt = endAt;
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
    public ImmutableMap<Capacity, Number> getAllCapacityValue(long time) {
        if (!this.isWork(time))
            return ImmutableMap.of();
        return capacityMap;
    }

    @Override
    public Set<Capacity> getSupplyCapacities(long time) {
        if (!this.isWork(time))
            return ImmutableSet.of();
        return capacityMap.keySet();
    }

    @Override
    public boolean isHasValue(Capacity capacity, long time) {
        if (!this.isWork(time))
            return false;
        return getAllCapacityValue().containsKey(capacity);
    }

    @Override
    public Number getValue(Capacity capacity, long time, Number defaultValue) {
        if (!this.isWork(time))
            return defaultValue;
        Number number = getAllCapacityValue().get(capacity);
        return number == null ? defaultValue : number;
    }


    @Override
    public Number getValue(Capacity capacity, long time) {
        if (!this.isWork(time))
            return null;
        return getAllCapacityValue().get(capacity);
    }

    @Override
    public CapacitySupplyType getSupplyType() {
        return type;
    }

    @Override
    public DateTime getEndAt() {
        return endAt;
    }

}