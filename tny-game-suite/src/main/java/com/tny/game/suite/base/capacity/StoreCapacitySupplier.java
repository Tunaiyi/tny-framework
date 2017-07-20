package com.tny.game.suite.base.capacity;


import java.util.Map;
import java.util.stream.Stream;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.base.capacity.ExpireCapacitiable.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface StoreCapacitySupplier extends ExpireCapacitySupplier {

    static StoreCapacitySupplier saveBySupply(CapacitySupplierType type, long id, int itemID, long playerID, CapacitySupply supply, long expireAt) {
        return new StoreByCopyCapacitySupplier(type, id, itemID, playerID, supply.getAllValues(), expireAtOf(supply, expireAt));
    }

    static StoreCapacitySupplier saveBySupplier(CapacitySupplier supplier, long expireAt) {
        if (supplier instanceof StoreCapacitySupplier) {
            if (expireAt == 0 || ((StoreCapacitySupplier) supplier).getExpireAt() == expireAt)
                return as(supplier);
        }
        if (supplier instanceof ComboCapacitySupplier)
            return saveBySupplier(as(supplier, ComboCapacitySupplier.class), expireAt);
        return new StoreByCopyCapacitySupplier(
                supplier.getSupplierType(),
                supplier.getID(),
                supplier.getItemID(),
                supplier.getPlayerID(),
                supplier.getAllValues(),
                expireAtOf(supplier, expireAt));
    }

    static StoreCapacitySupplier saveByCapacities(CapacitySupplierType type, long id, int itemID, long playerID, Map<Capacity, Number> capacityMap, long expireAt) {
        return new StoreByCopyCapacitySupplier(type, id, itemID, playerID, capacityMap, expireAt > 0 ? expireAt : -1);
    }

    static StoreCapacitySupplier saveBySupplier(ComboCapacitySupplier supplier, CapacityVisitor visitor, long expireAt) {
        if (supplier instanceof StoreCapacitySupplier) {
            if (expireAt == 0 || ((StoreCapacitySupplier) supplier).getExpireAt() == expireAt)
                return as(supplier);
        }
        return saveByDependSuppliers(
                supplier.getSupplierType(),
                supplier.getID(),
                supplier.getItemID(),
                supplier.dependSuppliersStream(),
                visitor,
                expireAtOf(supplier, expireAt));
    }

    static StoreCapacitySupplier saveByDependSuppliers(CapacitySupplierType type, long id, int itemID, Stream<? extends CapacitySupplier> suppliers, CapacityVisitor visitor, long expireAt) {
        return new StoreByCopyComboCapacitySupplier(
                type, id, itemID,
                suppliers.filter(CapacitySupplier::isSupplying)
                        .map(CapacitySupplier::getID),
                visitor,
                expireAt > 0 ? expireAt : -1);
    }

    static StoreCapacitySupplier saveByDependSupplierIDs(CapacitySupplierType type, long id, int itemID, Stream<Long> suppliers, CapacityVisitor visitor, long expireAt) {
        return new StoreByCopyComboCapacitySupplier(type, id, itemID, suppliers, visitor, expireAt > 0 ? expireAt : -1);
    }


    static StoreCapacitySupplier linkBySupplier(CapacitySupplier supplier, long expireAt) {
        if (supplier instanceof StoreCapacitySupplier) {
            if (expireAt == 0 || ((StoreCapacitySupplier) supplier).getExpireAt() == expireAt)
                return as(supplier);
        }
        return new StoreByLinkCapacitySupplier(supplier, expireAtOf(supplier, expireAt));
    }


    default boolean isLinked() {
        return false;
    }

    void expireAt(long at);

}