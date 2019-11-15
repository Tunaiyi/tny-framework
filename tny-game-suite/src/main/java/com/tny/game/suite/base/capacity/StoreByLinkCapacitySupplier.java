package com.tny.game.suite.base.capacity;


import com.google.common.base.MoreObjects;
import com.tny.game.base.item.*;

import java.util.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByLinkCapacitySupplier extends BaseStoreCapacitiable implements StoreCapacitySupplier {

    private CapacitySupplier supplier;

    StoreByLinkCapacitySupplier(CapacitySupplier supplier, long expireAt) {
        super(expireAt);
        this.supplier = supplier;
    }

    @Override
    public long getId() {
        return supplier.getId();
    }

    @Override
    public int getItemId() {
        return supplier.getItemId();
    }

    @Override
    public long getPlayerId() {
        return supplier.getPlayerId();
    }

    @Override
    public CapacitySupplierType getSupplierType() {
        return supplier.getSupplierType();
    }

    @Override
    public boolean isHasValue(Capacity capacity) {
        return supplier.isHasValue(capacity);
    }

    @Override
    public Number getValue(Capacity capacity, Number defaultValue) {
        return supplier.getValue(capacity, defaultValue);
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        return supplier.getAllCapacityGroups();
    }

    @Override
    public Number getValue(Capacity capacity) {
        return getValue(capacity, null);
    }

    @Override
    public Map<Capacity, Number> getAllValues() {
        return supplier.getAllValues();
    }

    @Override
    public boolean isLinked() {
        return true;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", supplier.getId())
                          .add("itemId", supplier.getItemId())
                          .add("name", ItemModels.name(supplier.getItemId()))
                          .toString();
    }

}